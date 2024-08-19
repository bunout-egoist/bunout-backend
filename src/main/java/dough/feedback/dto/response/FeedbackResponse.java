package dough.feedback.dto.response;

import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedbackResponse {

    private final Integer level;
    private final String burnoutName;

    public static FeedbackResponse from(final Member member) {
        return new FeedbackResponse(
                member.getLevel().getLevel(),
                member.getBurnout().getName()
        );
    }
}
