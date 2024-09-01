package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedQuestDetailResponse {

    private String imageUrl;
    private String content;
    private String questType;

    public static CompletedQuestDetailResponse of(final Quest quest, final String imageUrl) {
        return new CompletedQuestDetailResponse(
                imageUrl,
                quest.getContent(),
                quest.getQuestType().getCode());
    }
}
