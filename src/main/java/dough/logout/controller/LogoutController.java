package dough.logout.controller;

import dough.logout.dto.request.DeleteAccessTokenRequest;
import dough.logout.dto.response.DeleteAccessTokenResponse;
import dough.logout.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LogoutController {

    private final LogoutService logoutService;

    @DeleteMapping("/logout")
    public ResponseEntity<DeleteAccessTokenResponse> logout(@RequestBody DeleteAccessTokenRequest deleteAccessTokenRequest) {
        DeleteAccessTokenResponse deleteAccessTokenResponse = logoutService.logout(deleteAccessTokenRequest);
        return ResponseEntity.ok(deleteAccessTokenResponse);
    }
}
