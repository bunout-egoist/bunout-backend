package dough.dashboard.dto.response;

import dough.quest.dto.CompletedCountDateElement;
import dough.quest.dto.response.CompletedCountDateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@RequiredArgsConstructor
public class DashboardResponse {


    private final List<CompletedCountDateResponse> completedCountDateResponse;
    private final Long completedAllQuestsCount;
    private final Set<String> highestAverageCompletionDay;
    private final Long averageCompletion;

    public static DashboardResponse of(
            final List<CompletedCountDateElement> completedCountDateElements,
            final Long completedAllQuestsCount,
            final Map<String, Long> highestAverageCompletionDays,
            final Long averageCompletion
    ) {
        final List<CompletedCountDateResponse> dateCompletedCountDateRespons = completedCountDateElements.stream()
                .map(element -> CompletedCountDateResponse.of(element.getCompletedAt(), element.getDailyAndFixedCount()))
                .toList();

        final Set<String> dayOfWeeks = highestAverageCompletionDays.keySet();

        return new DashboardResponse(
                dateCompletedCountDateRespons,
                completedAllQuestsCount,
                dayOfWeeks,
                averageCompletion
        );
    }
}
