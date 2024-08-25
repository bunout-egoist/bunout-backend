package dough.member.dto.response;

import dough.member.domain.Member;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class MemberInfoResponse {

    private final Long id;
    private final String nickname;
    private final Long burnoutId;
    private final Long fixedQuestId;
    private final Integer level;

    public static MemberInfoResponse of(final Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getNickname(),
                member.getBurnout().getId(),
                member.getQuest().getId(),
                member.getLevel().getLevel()
        );
    }
}
