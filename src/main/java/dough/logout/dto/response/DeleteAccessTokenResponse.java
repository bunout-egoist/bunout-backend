package dough.logout.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dough.member.domain.Member;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class DeleteAccessTokenResponse {

    @JsonProperty("id")
    private final Long id;

    @JsonProperty("nickname")
    private final String nickname;

    public static DeleteAccessTokenResponse from(Member member) {
        return new DeleteAccessTokenResponse(member.getId(), member.getNickname());
    }
}
