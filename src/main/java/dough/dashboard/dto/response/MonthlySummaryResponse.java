package dough.dashboard.dto.response;

import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.response.CompletedQuestsCountDetailResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class MonthlySummaryResponse {

    private final List<CompletedQuestsCountDetailResponse> countDetails;
    private final Long completedAllQuestsDateCount;
    private final Set<String> highestAverageCompletionDay;
    private final Long averageCompletion;

    public static MonthlySummaryResponse of(
            final List<CompletedQuestsCountElement> completedQuestsCountDateElements,
            final Long completedAllQuestsDateCount,
            final Set<String> highestAverageCompletionDay,
            final Long averageCompletion
    ) {
        final List<CompletedQuestsCountDetailResponse> dateCompletedCountDateResponses = completedQuestsCountDateElements.stream()
                .map(element -> CompletedQuestsCountDetailResponse.of(element.getCompletedDate(), element.getBY_TYPECount()))
                .toList();

        return new MonthlySummaryResponse(
                dateCompletedCountDateResponses,
                completedAllQuestsDateCount,
                highestAverageCompletionDay,
                averageCompletion
        );
    }
}
