package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class TotalAndStatisticsResponse {

    private final Long dailyTotal;
    private final Long specialTotal;
    private final Set<String> highestAverageCompletionDay;
    private final Long averageCompletion;

    public static TotalAndStatisticsResponse of(
            final Long dailyTotal,
            final Long specialTotal,
            final Set<String> highestAverageCompletionDay,
            final Long averageCompletion
    ) {
        return new TotalAndStatisticsResponse(
                dailyTotal,
                specialTotal,
                highestAverageCompletionDay,
                averageCompletion
        );
    }
}
