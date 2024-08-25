package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TodayQuestResponse {

    private final String content;
    private final String questType;

    public static TodayQuestResponse of(final Quest quest) {
        return new TodayQuestResponse(
                quest.getContent(),
                quest.getQuestType().getCode()
        );
    }
}
