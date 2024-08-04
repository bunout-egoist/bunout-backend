package dough.dashboard.dto.response;

import dough.quest.dto.CompletedQuestCountElement;
import dough.quest.dto.DateCompletedQuestCountElement;
import dough.quest.dto.response.CompletedQuestCountResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DashboardResponse {

    private final Long specialQuestCount;
    private final Long dailyAndFixedQuestCount;
    private final List<DateCompletedQuestCountElement> dailyQuestCompletionResponses;
    private final Long completedThreeQuestsDate;
    private final Long highestAverageCompletionDay;
    private final Long averageCompletion;

    public static DashboardResponse of(
            final CompletedQuestCountElement completedQuestCountElement,
            final List<DateCompletedQuestCountElement> dateCompletedQuestCountElements,

            ) {

        return new DashboardResponse(


        )


    }


}
