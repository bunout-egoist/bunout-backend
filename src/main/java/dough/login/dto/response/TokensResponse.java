package dough.login.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokensResponse {
    private String jwtToken;
    private String refreshToken;

    public TokensResponse(String jwtToken, String refreshToken) {
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }
}
