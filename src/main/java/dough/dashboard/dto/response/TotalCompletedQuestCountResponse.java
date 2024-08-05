package dough.dashboard.dto.response;

import dough.dashboard.dto.CompletedQuestCountElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalCompletedQuestCountResponse {

    private Long dailyAndFixedCount;
    private Long specialCount;

    public static TotalCompletedQuestCountResponse of(final List<CompletedQuestCountElement> dashboards) {
        return new TotalCompletedQuestCountResponse(
                getDailyAndFixedCount(dashboards),
                getSpecialCount(dashboards)
        );
    }

    private static Long getDailyAndFixedCount(final List<CompletedQuestCountElement> dashboards) {
        return dashboards.stream()
                .mapToLong(dashboard -> dashboard.getDailyCount() + dashboard.getFixedCount())
                .sum();
    }

    private static Long getSpecialCount(final List<CompletedQuestCountElement> dashboards) {
        return dashboards.stream()
                .mapToLong(CompletedQuestCountElement::getSpecialCount)
                .sum();
    }
}
