package dough.feedback.dto.response;

import dough.level.domain.MemberLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedbackResponse {

    private final Integer currentLevel;
    private final Boolean isLevelUp;
    private final String imageUrl;

    public static FeedbackResponse of(final MemberLevel memberLevel, final String imageUrl) {
        return new FeedbackResponse(
                memberLevel.getLevel().getLevel(),
                memberLevel.getIsLevelUp(),
                imageUrl
        );
    }
}