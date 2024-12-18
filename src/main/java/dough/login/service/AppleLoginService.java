package dough.login.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.exception.BadRequestException;
import dough.login.domain.LoginInfo;
import dough.login.dto.response.ApplePublicKeyResponse;
import dough.login.dto.response.AppleTokenResponse;
import dough.login.infrastructure.oauth.LoginApiClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static dough.global.exception.ExceptionCode.FAIL_TO_APPLE_LOGIN;
import static dough.global.exception.ExceptionCode.FAIL_TO_GET_PUBLIC_KEY;
import static dough.login.domain.type.SocialLoginType.APPLE;

@Service
@RequiredArgsConstructor
public class AppleLoginService {

    private final LoginApiClient loginApiClient;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    public LoginInfo login(final String idToken, final String authorizationCode) {
        try {
            final Claims claims = getClaimsFromAppleToken(idToken);
            final String socialLoginId = claims.getSubject();

            final AppleTokenResponse appleTokenResponse = loginApiClient.getAppleToken(tokenRequestParams(authorizationCode));

            return new LoginInfo(socialLoginId, appleTokenResponse.getRefreshToken(), APPLE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new BadRequestException(FAIL_TO_APPLE_LOGIN);
        }
    }

    public void revoke(final String appleToken) throws IOException {
        final String clientSecret = makeClientSecret();
        loginApiClient.revokeToken(clientSecret, appleToken, clientId);
    }

    private Claims getClaimsFromAppleToken(final String identityToken) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, JsonProcessingException {
        final ApplePublicKeyResponse applePublicKeyResponse = loginApiClient.getAppleAuthPublicKey();

        final String identityTokenHeader = identityToken.split("\\.")[0];
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
    }

    public String makeClientSecret() throws IOException {
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

    private MultiValueMap<String, String> tokenRequestParams(final String code) throws IOException {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("code", code);
        params.add("client_secret", makeClientSecret());
        return params;
    }
}