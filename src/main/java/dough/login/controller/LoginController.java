package dough.login.controller;

import dough.login.domain.Accessor;
import dough.login.domain.Auth;
import dough.login.dto.request.SignUpRequest;
import dough.login.dto.response.AccessTokenResponse;
import dough.login.dto.response.LoginResponse;
import dough.login.service.LoginService;
import dough.member.dto.response.MemberInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/auth/login/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(
            @RequestParam("code") final String code
    ) {
        final LoginResponse loginResponse = loginService.login(code);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/auth/login/apple")
    public ResponseEntity<LoginResponse> appleLogin(
            @RequestParam("idToken") final String idToken,
            @RequestParam("authorizationCode") final String authorizationCode
    ) {
        final LoginResponse loginResponse = loginService.login(idToken, authorizationCode);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PutMapping("/signup/complete")
    public ResponseEntity<MemberInfoResponse> completeSignup(
            @Auth final Accessor accessor,
            @RequestBody @Valid final SignUpRequest signUpRequest
    ) {
        final MemberInfoResponse memberInfoResponse = loginService.completeSignup(accessor.getMemberId(), signUpRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> renewAccessToken() {
        final AccessTokenResponse accessTokenResponse = loginService.renewAccessToken();
        return ResponseEntity.ok().body(accessTokenResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@Auth final Accessor accessor) {
        loginService.logout(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/signout")
    public ResponseEntity<Void> signout(@Auth final Accessor accessor) throws IOException {
        loginService.signout(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
