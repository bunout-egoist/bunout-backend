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
    private final String activity;
    private final String description;
    private final String questType;
    private final String placeKeyword;
    private final String participationKeyword;
    private final String questStatus;
    private final String imageUrl;

    public static TodayQuestResponse of(final SelectedQuest selectedQuest) {
        final Quest quest = selectedQuest.getQuest();
        final String imageUrl = selectedQuest.getFeedback() != null ? selectedQuest.getFeedback().getImageUrl() : null;

        if (quest.getQuestType().equals(BY_TYPE)) {
            return byTypeResponse(selectedQuest, quest, imageUrl);
        }
        return fixedAndSpecialResponse(selectedQuest, quest, imageUrl);
    }

    public static TodayQuestResponse byTypeResponse(final SelectedQuest selectedQuest, final Quest quest, String imageUrl) {
        return new TodayQuestResponse(
                selectedQuest.getId(),
                quest.getActivity(),
                quest.getDescription(),
                quest.getQuestType().getCode(),
                PlaceType.getMappedPlaceType(quest.getKeyword().getIsOutside()).getCode(),
                ParticipationType.getMappedParticipationType(quest.getKeyword().getIsGroup()).getCode(),
                selectedQuest.getQuestStatus().toString(),
                imageUrl
        );
    }

    public static TodayQuestResponse fixedAndSpecialResponse(final SelectedQuest selectedQuest, final Quest quest, final String imageUrl) {
        return new TodayQuestResponse(
                selectedQuest.getId(),
                quest.getActivity(),
                quest.getDescription(),
                quest.getQuestType().getCode(),
                null,
                null,
                selectedQuest.getQuestStatus().toString(),
                imageUrl
        );
    }
}
