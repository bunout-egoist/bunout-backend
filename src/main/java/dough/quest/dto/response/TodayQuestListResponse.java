package dough.quest.dto.response;

import dough.keyword.domain.Keyword;
import dough.quest.domain.SelectedQuest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class TodayQuestListResponse {

    private final String participationCode;
    private final String placeCode;
    private final List<TodayQuestResponse> quests;

    public static TodayQuestListResponse of(
            final String participationCode,
            final String placeCode,
            final List<SelectedQuest> todayQuests
    ) {
        final List<TodayQuestResponse> todayQuestResponses = todayQuests.stream()
                .map(todayQuest -> TodayQuestResponse.of(todayQuest.getQuest()))
                .toList();

        return new TodayQuestListResponse(
                participationCode,
                placeCode,
                todayQuestResponses
        );
    }
}
