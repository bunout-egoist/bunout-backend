package dough.dashboard.dto.response;

import dough.dashboard.domain.Dashboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CompletedQuestCountResponse {

    private final LocalDate date;
    private final Long completedQuestCount;

    public static CompletedQuestCountResponse of(final Dashboard dashboard) {
        return new CompletedQuestCountResponse(
                dashboard.getCompletedAt(),
                dashboard.getDailyCount() + dashboard.getFixedCount()
        );
    }
}