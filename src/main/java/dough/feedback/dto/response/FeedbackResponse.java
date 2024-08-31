package dough.feedback.dto.response;

import dough.level.domain.MemberLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedbackResponse {

    private final Integer currentLevel;
    private final Boolean isLevelUp;

    public static FeedbackResponse of(final MemberLevel memberLevel) {
        return new FeedbackResponse(
                memberLevel.getLevels().get(0).getLevel(),
                memberLevel.getIsLevelUp()
        );
    }
}
