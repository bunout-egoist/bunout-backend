package dough.login.controller;

import dough.login.dto.request.SignUpRequest;
import dough.login.dto.response.AccessTokenResponse;
import dough.login.dto.response.LoginResponse;
import dough.login.service.LoginService;
import dough.member.dto.response.MemberInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/auth/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable("provider") final String provider,
            @RequestParam("code") final String code) {
        final LoginResponse loginResponse = loginService.login(provider, code);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PutMapping("/signup/complete")
    public ResponseEntity<MemberInfoResponse> completeSignup(@RequestBody @Valid final SignUpRequest signUpRequest) {
        final MemberInfoResponse memberInfoResponse = loginService.completeSignup(signUpRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout() {
        loginService.logout();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/signout")
    public ResponseEntity<Void> signout() throws IOException {
        loginService.signout();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> createNewAccessToken(@RequestHeader("RefreshToken") final String refreshToken) {
        final AccessTokenResponse accessTokenResponse = loginService.renewAccessToken(refreshToken);
        return ResponseEntity.ok().body(accessTokenResponse);
    }
}
