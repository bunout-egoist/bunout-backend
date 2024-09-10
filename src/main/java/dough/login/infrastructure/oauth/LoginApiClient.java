package dough.login.infrastructure.oauth;

import dough.login.dto.response.ApplePublicKeyResponse;
import dough.login.dto.response.AppleTokenResponse;
import dough.login.dto.response.KakaoMemberResponse;
import dough.login.dto.response.KakaoTokenResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public interface LoginApiClient {

    @PostExchange("https://kauth.kakao.com/oauth/token")
    KakaoTokenResponse getKakaoToken(@RequestParam final MultiValueMap<String, String> params);

    @GetExchange("https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse getKakaoMemberInfo(@RequestHeader(name = AUTHORIZATION) final String bearerToken);

    @GetExchange(value = "https://appleid.apple.com/auth/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostExchange(value = "https://appleid.apple.com/auth/token")
    AppleTokenResponse getAppleToken(@RequestParam final MultiValueMap<String, String> params);

    @PostExchange(value = "https://appleid.apple.com/auth/revoke")
    void revokeToken(@RequestParam("client_secret") String clientSecret,
                     @RequestParam("token") String refreshToken,
                     @RequestParam("client_id") String clientId);
}
