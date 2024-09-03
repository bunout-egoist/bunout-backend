package dough.login.controller;

import dough.login.dto.response.KakaoTokenResponse;
import dough.login.dto.response.TokensResponse;
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

    @PostMapping("/login/kakao")
    public ResponseEntity<?> loginWithKakao(@RequestBody Map<String, String> request) {

        KakaoTokenResponse kakaoTokenResponse = kakaoLoginService.kakaoLogin(request);

        return ResponseEntity.ok(new TokensResponse(kakaoTokenResponse.getAccess_token(), kakaoTokenResponse.getRefresh_token()));
    }

}
