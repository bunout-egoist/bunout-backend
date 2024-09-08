package dough.login.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.exception.BadRequestException;
import dough.global.exception.LoginException;
import dough.level.domain.Level;
import dough.level.domain.repository.LevelRepository;
import dough.login.LoginApiClient;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.response.ApplePublicKeyResponse;
import dough.login.dto.response.AppleTokenResponse;
import dough.login.dto.response.LoginResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static dough.global.exception.ExceptionCode.*;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.KAKAO;


@Service
@RequiredArgsConstructor
public class AppleLoginService {

    private final LoginApiClient loginApiClient;
    private final TokenProvider tokenProvider;

    private final LoginService loginService;
    private final MemberRepository memberRepository;
    private final LevelRepository levelRepository;

    @Value("${apple.client-id}")
    private String clientId;
    @Value("${apple.team-id}")
    private String teamId;
    @Value("${apple.key-id}")
    private String keyId;
    @Value("${apple.redirect-uri}")
    private String redirectUri;

    public LoginResponse appleLogin(final String code) {
        try {
            final Claims claims = getClaimsFromAppleToken(code);
            final String socialLoginId = claims.getSubject();

            final Level level = levelRepository.findByLevel(1)
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_LEVEL_ID));

            final Member member = memberRepository.findBySocialLoginId(clientId)
                    .orElseGet(() -> {
                        final Member newMember = new Member(
                                socialLoginId,
                                KAKAO,
                                MEMBER,
                                level
                        );
                        return newMember;
                    });

            final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", redirectUri);
            params.add("client_id", clientId);
            params.add("code", code);
            params.add("client_secret", makeClientSecret());

            final AppleTokenResponse appleTokenResponse = loginApiClient.getAppleToken(params);

            final String memberAccessToken = tokenProvider.generateToken(member, Duration.ofHours(1));
            final String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));

            member.updateRefreshToken(refreshToken);
            memberRepository.save(member);

            return LoginResponse.of(memberAccessToken, member, false);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new BadRequestException(FAIL_TO_APPLE_LOGIN);
        }
    }

    private Claims getClaimsFromAppleToken(String identityToken) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, JsonProcessingException {
        try {
            final ApplePublicKeyResponse applePublicKeyResponse = loginApiClient.getAppleAuthPublicKey();

            final String identityTokenHeader = identityToken.substring(0, identityToken.indexOf("."));
            final Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(identityTokenHeader), "UTF-8"), Map.class);
            final ApplePublicKeyResponse.Key key = applePublicKeyResponse.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new BadRequestException(FAIL_TO_GET_PUBLIC_KEY));

            final byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            final byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            final BigInteger n = new BigInteger(1, nBytes);
            final BigInteger e = new BigInteger(1, eBytes);

            final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            final KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken)
                    .getBody();

            return claims;

        } catch (MalformedJwtException e) {
            throw new LoginException(MALFORMED_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new LoginException(EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new LoginException(UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new LoginException(INVALID_TOKEN);
        }
    }

    private String makeClientSecret() throws IOException {
        final Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException {
        final ClassPathResource resource = new ClassPathResource("keys/AuthKey_D69RVU73XV.p8");
        final String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        final Reader pemReader = new StringReader(privateKey);
        final PEMParser pemParser = new PEMParser(pemReader);
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        final PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }
}