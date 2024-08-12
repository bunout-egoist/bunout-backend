package dough.dashboard.dto.response;

import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.response.CompletedQuestCountDetailResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MonthlySummaryResponse {


    private final List<CompletedQuestCountDetailResponse> countDetails;
    private final Long completedAllQuestsDateCount;
    private final Set<String> highestAverageCompletionDay;
    private final Long averageCompletion;

    public static MonthlySummaryResponse of(
            final List<CompletedQuestsCountElement> completedQuestsCountDateElements,
            final Long completedAllQuestsDateCount,
            final Set<String> highestAverageCompletionDay,
            final Long averageCompletion
    ) {
        final List<CompletedQuestCountDetailResponse> dateCompletedCountDateRespons = completedQuestsCountDateElements.stream()
                .map(element -> CompletedQuestCountDetailResponse.of(element.getCompletedDate(), element.getDailyAndFixedCount()))
                .toList();

        return new MonthlySummaryResponse(
                dateCompletedCountDateRespons,
                completedAllQuestsDateCount,
                highestAverageCompletionDay,
                averageCompletion
        );
    }
}
