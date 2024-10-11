package dough.quest.dto.response;

import dough.burnout.domain.Burnout;
import dough.keyword.KeywordCode;
import dough.member.domain.Member;
import dough.quest.domain.SelectedQuest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TodayQuestListResponse {

    private final Long burnoutId;
    private final Integer level;
    private final String placeKeyword;
    private final String participationKeyword;
    private final List<TodayQuestResponse> todayQuests;

    public static TodayQuestListResponse of(
            final Burnout burnout,
            final Member member,
            final KeywordCode keywordCode,
            final List<SelectedQuest> todayQuests
    ) {
        final List<TodayQuestResponse> todayQuestResponses = todayQuests.stream()
                .map(todayQuest -> TodayQuestResponse.of(todayQuest))
                .toList();

        return new TodayQuestListResponse(
                burnout.getId(),
                member.getLevel().getLevel(),
                keywordCode.getPlaceCode(),
                keywordCode.getParticipationCode(),
                todayQuestResponses
        );
    }
}
