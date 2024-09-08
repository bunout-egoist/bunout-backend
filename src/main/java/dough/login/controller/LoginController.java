package dough.login.controller;

import dough.login.dto.response.LoginResponse;
import dough.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable("provider") final String provider,
            @RequestParam("code") final String code) {
        final LoginResponse loginResponse = loginService.login(provider, code);
        return ResponseEntity.ok().body(loginResponse);
    }
}
