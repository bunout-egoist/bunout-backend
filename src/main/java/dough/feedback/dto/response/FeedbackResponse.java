package dough.feedback.dto.response;


import dough.feedback.domain.Feedback;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedbackResponse {

    private final Long id;
    private final String imageUrl;
    private final Integer difficulty;
    private final Long memberId;
    private final Long selectedQuestId;

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getImageUrl(),
                feedback.getDifficulty(),
                feedback.getMember().getId(),
                feedback.getSelectedQuest().getId()
        );
    }
}
