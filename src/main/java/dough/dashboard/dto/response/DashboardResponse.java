package dough.dashboard.dto.response;

import dough.quest.dto.DateCompletedQuestCountElement;
import dough.quest.dto.response.CompletedQuestCountResponse;
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


    private final List<CompletedQuestCountResponse> completedQuestCountResponses;
    private final LocalDate completedThreeQuestsDate;
    private final Enum highestAverageCompletionDay;
    private final Long averageCompletion;

    public static DashboardResponse of(
            final List<DateCompletedQuestCountElement> dateCompletedQuestCountElements
    ) {
        final List<CompletedQuestCountResponse> dateCompletedQuestCountResponses = dateCompletedQuestCountElements.stream()
                .map(element -> CompletedQuestCountResponse.of(element.getDate(), element.getDailyAndFixedCount()))
                .toList();

        return new DashboardResponse(
                dateCompletedQuestCountResponses,
                getCompletedThreeQuestsDate(dateCompletedQuestCountElements),
                getHighestAverageCompletionDay(dateCompletedQuestCountElements),
                getAverageCompletion(dateCompletedQuestCountElements)
        );
    }

    private static LocalDate getCompletedThreeQuestsDate(final List<DateCompletedQuestCountElement> dateCompletedQuestCountElements) {
        return dateCompletedQuestCountElements.stream()
                .filter(dashboard -> dashboard.getDailyAndFixedCount() == 3)
                .map(DateCompletedQuestCountElement::getDate)
                .collect(Collectors.toList())
                .get(0);
    }

    private static Long getAverageCompletion(final List<DateCompletedQuestCountElement> dateCompletedQuestCountElements) {
        final Long totalCount = dateCompletedQuestCountElements.stream()
                .mapToLong(dashboard -> dashboard.getDailyAndFixedCount() + dashboard.getSpecialCount())
                .sum();

        final LocalDate completedAt = dateCompletedQuestCountElements.get(0).getDate();
        final int month = completedAt.lengthOfMonth();
        return totalCount / (month * 3 + 12);
    }

    private static Enum getHighestAverageCompletionDay(final List<DateCompletedQuestCountElement> dateCompletedQuestCountElements) {
        final Map<Enum, Long> map = new HashMap<>();
        dateCompletedQuestCountElements.stream()
                .forEach(dashboard -> {
                    final Enum dayOfWeek = dashboard.getDate().getDayOfWeek();
                    final Long countByDayOfWeek = dashboard.getSpecialCount() + dashboard.getDailyAndFixedCount();
                    map.put(dayOfWeek, map.getOrDefault(dayOfWeek, 0L) + countByDayOfWeek);
                });

        // TODO 만약 그런 날이 없을 경우는?
        return Collections.max(map.keySet());
    }
}
