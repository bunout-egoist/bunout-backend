package dough.dashboard.dto.response;

import dough.dashboard.domain.Dashboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class DashboardResponse {

    private final Long dailyAndFixedCount;
    private final Long specialCount;
    private final List<CompletedQuestCountResponse> completedQuestCountResponses;
    private final LocalDate completedThreeQuestsDate;
    private final Enum highestAverageCompletionDay;
    private final Long averageCompletion;

    public static DashboardResponse of(
            final List<Dashboard> dashboards
    ) {
        final List<CompletedQuestCountResponse> dateCompletedQuestCountResponses = dashboards.stream()
                .map(dashboard -> CompletedQuestCountResponse.of(dashboard))
                .toList();

        return new DashboardResponse(
                getDailyAndFixedCount(dashboards),
                getSpecialCount(dashboards),
                dateCompletedQuestCountResponses,
                getCompletedThreeQuestsDate(dashboards),
                getHighestAverageCompletionDay(dashboards),
                getAverageCompletion(dashboards)
        );
    }

    private static Long getDailyAndFixedCount(final List<Dashboard> dashboards) {
        return dashboards.stream()
                .mapToLong(dashboard -> dashboard.getDailyCount() + dashboard.getFixedCount())
                .sum();
    }

    private static Long getSpecialCount(final List<Dashboard> dashboards) {
        return dashboards.stream()
                .mapToLong(Dashboard::getSpecialCount)
                .sum();
    }

    private static LocalDate getCompletedThreeQuestsDate(final List<Dashboard> dashboards) {
        return dashboards.stream()
                .filter(dashboard -> dashboard.getDailyCount() + dashboard.getFixedCount() == 3)
                .map(Dashboard::getCompletedAt)
                .collect(Collectors.toList())
                .get(0);
    }

    private static Long getAverageCompletion(final List<Dashboard> dashboards) {
        final Long totalCount = dashboards.stream()
                .mapToLong(dashboard -> dashboard.getDailyCount() + dashboard.getFixedCount() + dashboard.getSpecialCount())
                .sum();

        final LocalDate completedAt = dashboards.get(0).getCompletedAt();
        final int month = completedAt.lengthOfMonth();
        return totalCount / (month * 3 + 12);
    }

    private static Enum getHighestAverageCompletionDay(final List<Dashboard> dashboards) {
        final Map<Enum, Long> map = new HashMap<>();
        dashboards.stream()
                .forEach(dashboard -> {
                    final Enum dayOfWeek = dashboard.getCompletedAt().getDayOfWeek();
                    final Long countByDayOfWeek = dashboard.getSpecialCount() + dashboard.getDailyCount() + dashboard.getFixedCount();
                    map.put(dayOfWeek, map.getOrDefault(dayOfWeek, 0L) + countByDayOfWeek);
                });

        // TODO 만약 그런 날이 없을 경우는?
        return Collections.max(map.keySet());
    }
}