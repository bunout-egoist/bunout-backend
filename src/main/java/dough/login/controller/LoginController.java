package dough.login.controller;

import dough.login.dto.response.LoginResponse;
import dough.login.service.AppleLoginService;
import dough.login.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;

    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponse> loginWithKakao(@RequestParam("code") final String code) {
        final LoginResponse loginResponse = kakaoLoginService.kakaoLogin(code);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/login/apple")
    public ResponseEntity<?> loginWithApple(@RequestParam("code") final String code) {
        final LoginResponse loginResponse = appleLoginService.appleLogin(code);
        return ResponseEntity.ok().body(loginResponse);
    }
}
