package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuestResponse {

    private final Long id;
    private final String description;
    private final String activity;
    private final String questType;
    private final Integer difficulty;

    public static QuestResponse of(final Quest quest) {
        return new QuestResponse(
                quest.getId(),
                quest.getDescription(),
                quest.getActivity(),
                quest.getQuestType().getCode(),
                quest.getDifficulty());
    }
}
