package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FixedQuestResponse {

    private final Long questId;
    private final String content;

    public static FixedQuestResponse of(final Quest quest) {
        return new FixedQuestResponse(
                quest.getId(),
                quest.getContent()
        );
    }
}
