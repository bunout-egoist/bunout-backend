package dough.quest.dto.response;

import dough.feedback.domain.Feedback;
import dough.quest.domain.Quest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedQuestDetailResponse {

    private Long id;
    private String imageUrl;
    private String description;
    private String activity;
    private String questType;

        public static CompletedQuestDetailResponse of(final Quest quest, final Feedback feedback) {
            return new CompletedQuestDetailResponse(
                    feedback.getId(),
                    feedback.getImageUrl(),
                    quest.getDescription(),
                    quest.getActivity(),
                    quest.getQuestType().getCode());
        }
}
