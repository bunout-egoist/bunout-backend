package dough.feedback.dto.response;

import dough.level.domain.MemberLevel;
import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedbackResponse {

    private final Integer exp;
    private final Integer currentLevel;
    private final Integer previousLevel;
    private final Boolean isLevelUp;

    public static FeedbackResponse of(final MemberLevel memberLevel) {
        final Member member = memberLevel.getMember();

        return new FeedbackResponse(
                member.getExp(),
                member.getLevel().getLevel(),
                memberLevel.getPreviousLevel(),
                memberLevel.getIsLevelUp()
        );
    }
}
