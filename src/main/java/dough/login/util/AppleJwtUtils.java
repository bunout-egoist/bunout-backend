package dough.login.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.exception.BadRequestException;
import dough.login.domain.client.AppleClient;
import dough.login.dto.response.ApplePublicKeyResponse;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static dough.global.exception.ExceptionCode.INTERNAL_SEVER_ERROR;

@Component
public class AppleJwtUtils {

    private final AppleClient appleClient;

    public AppleJwtUtils(AppleClient appleClient) {
        this.appleClient = appleClient;
    }

    public Claims getClaimsFromAppleToken(String identityToken) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, JsonProcessingException {
        ApplePublicKeyResponse response = appleClient.getAppleAuthPublicKey();

        String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
        Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
        ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

        byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
        KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 애플의 공개키를 사용해 클레임을 파싱, 여기서 유저 id를 얻을 수 있음
        Claims claims = Jwts.parserBuilder()  // parserBuilder()를 사용하여 파서 빌더를 생성합니다.
                .setSigningKey(publicKey)    // 공개키를 설정합니다.
                .build()                     // JwtParser를 생성합니다.
                .parseClaimsJws(identityToken)  // 토큰을 파싱합니다.
                .getBody();                  // Claims 객체를 가져옵니다.

        // 토큰 만료시간 검증
        Date expiration = claims.getExpiration();
        if(expiration.before(new Date())) {
            throw new BadRequestException(INTERNAL_SEVER_ERROR);
        }

        return claims;
    }
}
