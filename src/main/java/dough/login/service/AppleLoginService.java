package dough.login.service;

import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.AppleToken;
import dough.login.domain.RefreshToken;
import dough.login.domain.client.AppleClient;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.login.util.AppleJwtUtils;
import dough.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static dough.global.exception.ExceptionCode.INTERNAL_SEVER_ERROR;


@Service
@RequiredArgsConstructor
public class AppleLoginService {

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.redirect-uri}")
    private String redirectUri;


    private final AppleClient appleClient;
    private final AppleJwtUtils appleJwtUtils;
    private final TokenProvider tokenProvider;
    private final LoginService loginService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AppleToken.Response appleLogin(Map<String, String> request) {
        try {
            String identity_token = request.get("identity_token");

            Claims claims = appleJwtUtils.getClaimsFromAppleToken(identity_token);
            String socialLoginId = claims.getSubject();

            ////////// DB로직에 사용자 정보 저정하는 로직 추가
            Member member;
            try {
                member = loginService.findBySocialLoginId(socialLoginId);
            } catch (IllegalArgumentException e) {
                member = loginService.createMember(socialLoginId, SocialLoginType.APPLE, null, RoleType.MEMBER);
            }
            //////////
            String clientSecret = makeClientSecret();


            // AppleToken.Request 객체 생성
            AppleToken.Request tokenRequest = AppleToken.Request.of(
                    request.get("authorization_code"), // code: 인가 코드
                    clientId,                  // client_id: Apple에서 발급받은 클라이언트 ID
                    clientSecret,                      // client_secret: Apple 서버와 통신하기 위한 클라이언트 시크릿
                    "authorization_code",              // grant_type: "authorization_code"로 설정
                    null,                       // refresh_token: 초기에는 필요하지 않음
                    redirectUri
            );

            // AppleClient를 통해 Apple 서버에 토큰 요청
            AppleToken.Response tokenResponse = appleClient.getToken(tokenRequest);

            // 서버 jwt 액세스 토큰만 발급. 리프레시 토큰은 애플꺼 사용
            String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(1)); // 서버 jwt 엑세스 토큰
            String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));
            tokenResponse.setAccess_token(jwtToken); // 서버 자체 accessToken으로 변경
            tokenResponse.setRefresh_token(refreshToken); // 서버 자체 refreshToken으로 변경

            // 서버에 애플 리프레시 토큰 저장, 향후 revoke시 사용
            RefreshToken refreshTokenEntity = new RefreshToken(member, tokenResponse.getRefreshToken());
            refreshTokenRepository.save(refreshTokenEntity);

            // 반환할 DTO 생성 및 반환 (토큰 관련 정보 포함)
            return tokenResponse;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new BadRequestException(INTERNAL_SEVER_ERROR);
        }

    }

    public String makeClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource("keys/AuthKey_WNYU6YAVJM.p8");
        String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }

}
