package dough.logout.dto.request;

import lombok.Getter;

@Getter
public class DeleteAccessTokenRequest {
    private String accessToken;
}
