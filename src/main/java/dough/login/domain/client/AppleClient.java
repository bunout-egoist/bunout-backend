package dough.login.domain.client;

import dough.login.domain.AppleToken;
import dough.login.dto.response.ApplePublicKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth")
public interface AppleClient {
    @GetMapping(value = "/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    AppleToken.Response getToken(AppleToken.Request request);

    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    void revokeToken(@RequestParam("client_secret") String clientSecret,
                     @RequestParam("token") String refreshToken,
                     @RequestParam("client_id") String clientId);
}
