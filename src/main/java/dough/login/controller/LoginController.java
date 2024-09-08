package dough.login.controller;

import dough.login.domain.AppleToken;
import dough.login.dto.response.KakaoLoginResponse;
import dough.login.dto.response.TokensResponse;
import dough.login.service.AppleLoginService;
import dough.login.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;

    @PostMapping("/login/kakao")
    public ResponseEntity<KakaoLoginResponse> loginWithKakao(@RequestParam("code") final String code) {
        final KakaoLoginResponse kakaoLoginResponse = kakaoLoginService.kakaoLogin(code);
        return ResponseEntity.ok().body(kakaoLoginResponse);
    }

    @PostMapping("/login/apple")
    public ResponseEntity<?> loginWithApple(@RequestBody Map<String, String> request) {
        AppleToken.Response appleResponse = appleLoginService.appleLogin(request);
        return ResponseEntity.ok(new TokensResponse(appleResponse.getAccessToken(), appleResponse.getRefreshToken()));
    }
}
