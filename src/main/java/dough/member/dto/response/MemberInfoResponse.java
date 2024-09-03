package dough.member.dto.response;

import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberInfoResponse {

    private final Long id;
    private final String nickname;
    private final String burnoutName;
    private final Long fixedQuestId;
    private final Integer level;

    public static MemberInfoResponse of(final Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getNickname(),
                member.getBurnout().getName(),
                member.getQuest().getId(),
                member.getLevel().getLevel()
        );
    }
}
