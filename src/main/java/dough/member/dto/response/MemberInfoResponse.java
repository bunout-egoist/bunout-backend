package dough.member.dto.response;

import dough.member.domain.Member;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class MemberInfoResponse {

    private final String nickname;

    public static MemberInfoResponse from(final Member member) {
        return new MemberInfoResponse(member.getNickname());
    }
}
