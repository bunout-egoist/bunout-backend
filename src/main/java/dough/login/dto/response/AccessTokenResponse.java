package dough.login.dto.response;

import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class AccessTokenResponse {

    private String accessToken;
}
