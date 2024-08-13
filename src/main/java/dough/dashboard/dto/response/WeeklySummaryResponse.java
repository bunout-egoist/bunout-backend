package dough.dashboard.dto.response;

import dough.quest.domain.QuestFeedback;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class WeeklySummaryResponse {

    private final LocalDate completedDate;
    private final List<CompletedQuestDetailResponse> questDetails;
    private final Long dailyCount;

    public static WeeklySummaryResponse of(
            final LocalDate completedDate,
            final List<QuestFeedback> questFeedbacks,
            final Long dailyCount
    ) {
        final List<CompletedQuestDetailResponse> completedQuestDetailResponses = questFeedbacks.stream()
                .map(guestFeedback -> CompletedQuestDetailResponse.of(guestFeedback.getQuest(), guestFeedback.getImageUrl()))
                .toList();

        return new WeeklySummaryResponse(
                completedDate,
                completedQuestDetailResponses,
                dailyCount
        );
    }
}
