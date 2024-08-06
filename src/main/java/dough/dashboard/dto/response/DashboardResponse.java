package dough.dashboard.dto.response;

import dough.quest.dto.CompletedCountDateElement;
import dough.quest.dto.response.CompletedCountDateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class DashboardResponse {


    private final List<CompletedCountDateResponse> completedCountDates;
    private final Long completedAllQuestsCount;
    private final Set<String> highestAverageCompletionDay;
    private final Long averageCompletion;

    public static DashboardResponse of(
            final List<CompletedCountDateElement> completedCountDateElements,
            final Long completedAllQuestsCount,
            final Set<String> highestAverageCompletionDay,
            final Long averageCompletion
    ) {
        final List<CompletedCountDateResponse> dateCompletedCountDateRespons = completedCountDateElements.stream()
                .map(element -> CompletedCountDateResponse.of(element.getCompletedAt(), element.getDailyAndFixedCount()))
                .toList();

        return new DashboardResponse(
                dateCompletedCountDateRespons,
                completedAllQuestsCount,
                highestAverageCompletionDay,
                averageCompletion
        );
    }
}
