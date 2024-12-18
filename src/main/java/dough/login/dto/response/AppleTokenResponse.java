package dough.login.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppleTokenResponse {

    private String accessToken;
    private String expiresIn;
    private String idToken;
    private String refreshToken;
    private String tokenType;
    private String error;
}
