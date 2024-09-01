package dough.quest.dto.response;

import dough.keyword.domain.type.ParticipationType;
import dough.keyword.domain.type.PlaceType;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static dough.quest.domain.type.QuestType.BY_TYPE;

@Getter
@RequiredArgsConstructor
public class TodayQuestResponse {

    private final Long selectedQuestId;
    private final String content;
    private final String questType;
    private final String placeKeyword;
    private final String participationKeyword;

    public static TodayQuestResponse of(final SelectedQuest selectedQuest) {
        final Quest quest = selectedQuest.getQuest();

        if (quest.getQuestType().equals(BY_TYPE)) {
            return byTypeResponse(selectedQuest, quest);
        }
        return fixedAndSpecialResponse(selectedQuest, quest);
    }

    public static TodayQuestResponse byTypeResponse(final SelectedQuest selectedQuest, final Quest quest) {
        return new TodayQuestResponse(
                selectedQuest.getId(),
                quest.getContent(),
                quest.getQuestType().getCode(),
                PlaceType.getMappedPlaceType(quest.getKeyword().getIsOutside()).getCode(),
                ParticipationType.getMappedParticipationType(quest.getKeyword().getIsGroup()).getCode()
        );
    }

    public static TodayQuestResponse fixedAndSpecialResponse(final SelectedQuest selectedQuest, final Quest quest) {
        return new TodayQuestResponse(
                selectedQuest.getId(),
                quest.getContent(),
                quest.getQuestType().getCode(),
                null,
                null
        );
    }
}
