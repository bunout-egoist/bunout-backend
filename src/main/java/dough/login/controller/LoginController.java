package dough.login.controller;

import dough.login.dto.response.LoginResponse;
import dough.login.service.LoginService;
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

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout() throws IOException {
        loginService.logout();
        return ResponseEntity.noContent().build();
    }
}
