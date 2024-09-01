package dough.login.controller;

import dough.login.dto.request.CreateAccessTokenRequest;
import dough.login.dto.response.CreateAccessTokenResponse;
import dough.login.dto.response.TokensResponse;
import dough.login.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokensResponse> refreshExpiredTokens(@RequestBody CreateAccessTokenRequest request) {
        TokensResponse access_refresh_tokens = tokenService.refreshTokens(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(access_refresh_tokens);
    }
}
