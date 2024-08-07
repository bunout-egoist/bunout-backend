package dough.logout.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class DeleteAccessTokenRequest {
    private String accessToken;
}
