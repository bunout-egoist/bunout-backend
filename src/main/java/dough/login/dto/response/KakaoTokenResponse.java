package dough.login.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoTokenResponse {
    private final String access_token;
    private final String refresh_token;
}