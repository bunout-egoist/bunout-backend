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

    public static MonthlySummaryResponse of(
            final List<CompletedQuestsCountElement> completedQuestsCountDateElements,
            final Long completedAllQuestsDateCount
    ) {
        final List<CompletedQuestsCountDetailResponse> dateCompletedCountDateResponses = completedQuestsCountDateElements.stream()
                .map(element -> CompletedQuestsCountDetailResponse.of(element.getCompletedDate(), element.getByTypeCount()))
                .toList();

        return new MonthlySummaryResponse(
                dateCompletedCountDateResponses,
                completedAllQuestsDateCount
        );
    }
}
