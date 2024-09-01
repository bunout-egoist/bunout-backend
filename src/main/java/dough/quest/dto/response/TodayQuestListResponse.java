package dough.quest.dto.response;

import dough.keyword.KeywordCode;
import dough.quest.domain.SelectedQuest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TodayQuestListResponse {

    private final String placeKeyword;
    private final String participationKeyword;
    private final List<TodayQuestResponse> todayQuests;

    public static TodayQuestListResponse of(
            final KeywordCode keywordCode,
            final List<SelectedQuest> todayQuests
    ) {
        final List<TodayQuestResponse> todayQuestResponses = todayQuests.stream()
                .map(todayQuest -> TodayQuestResponse.of(todayQuest.getQuest()))
                .toList();

        return new TodayQuestListResponse(
                keywordCode.getPlaceCode(),
                keywordCode.getParticipationCode(),
                todayQuestResponses
        );
    }
}
