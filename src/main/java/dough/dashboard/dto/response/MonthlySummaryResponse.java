package dough.dashboard.dto.response;

import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.response.CompletedCountDateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MonthlySummaryResponse {


    private final List<CompletedCountDateResponse> completedCountDates;
    private final Long completedAllQuestsCount;
    private final Set<String> highestAverageCompletionDay;
    private final Long averageCompletion;

    public static MonthlySummaryResponse of(
            final List<CompletedQuestsCountElement> completedQuestsCountDateElements,
            final Long completedAllQuestsCount,
            final Set<String> highestAverageCompletionDay,
            final Long averageCompletion
    ) {
        final List<CompletedCountDateResponse> dateCompletedCountDateRespons = completedQuestsCountDateElements.stream()
                .map(element -> CompletedCountDateResponse.of(element.getCompletedDate(), element.getDailyAndFixedCount()))
                .toList();

        return new MonthlySummaryResponse(
                dateCompletedCountDateRespons,
                completedAllQuestsCount,
                highestAverageCompletionDay,
                averageCompletion
        );
    }
}
