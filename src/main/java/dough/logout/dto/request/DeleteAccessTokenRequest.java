package dough.logout.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class DeleteAccessTokenRequest {
    private String accessToken;
}
