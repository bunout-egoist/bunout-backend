package dough.dashboard.service;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.quest.domain.QuestFeedback;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedQuestElements;
import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.CompletedQuestsTotalElement;
import dough.quest.dto.response.TotalAndStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static dough.quest.domain.type.QuestType.SPECIAL;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final SelectedQuestRepository selectedQuestRepository;

    @Transactional(readOnly = true)
    public List<WeeklySummaryResponse> getWeeklySummary(final Long memberId, final LocalDate date) {
        final LocalDate startDate = date.minusDays(3);
        final LocalDate endDate = date.plusDays(3);
        final CompletedQuestElements completedQuestElements = new CompletedQuestElements(selectedQuestRepository.findCompletedQuestsByMemberIdAndDate(memberId, startDate, endDate));
        final Map<LocalDate, List<QuestFeedback>> questFeedbackMap = completedQuestElements.toQuestFeedbackMap();

        return getWeeklySummaryResponses(questFeedbackMap);
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(final Long memberId, final YearMonth yearMonth) {
        final int year = yearMonth.getYear();
        final int month = yearMonth.getMonthValue();
        final List<CompletedQuestsCountElement> completedQuestsCountElements = selectedQuestRepository.getCompletedQuestsCountByMemberIdAndDate(memberId, year, month);

        final Long completedAllQuestsDateCount = getCompletedAllQuestsDateCount(completedQuestsCountElements);

        return MonthlySummaryResponse.of(
                completedQuestsCountElements,
                completedAllQuestsDateCount
        );
    }

    @Transactional(readOnly = true)
    public TotalAndStatisticsResponse getCompletedQuestsTotalAndStatistics(final Long memberId) {
        final YearMonth currentYearMonth = YearMonth.now();

        final CompletedQuestsTotalElement completedQuestsTotalElement = selectedQuestRepository.getCompletedQuestsTotalByMemberId(memberId);
        final List<CompletedQuestsCountElement> completedQuestsCountElements = selectedQuestRepository.getCompletedQuestsCountByMemberIdAndDate(memberId, currentYearMonth.getYear(), currentYearMonth.getMonthValue());

        final Set<String> highestAverageCompletionDays = getHighestAverageCompletionDays(completedQuestsCountElements);
        final Long averageCompletion = getAverageCompletion(completedQuestsCountElements, currentYearMonth);

        return TotalAndStatisticsResponse.of(
                completedQuestsTotalElement.getDailyTotal(),
                completedQuestsTotalElement.getSpecialTotal(),
                highestAverageCompletionDays,
                averageCompletion
        );
    }

    private List<WeeklySummaryResponse> getWeeklySummaryResponses(Map<LocalDate, List<QuestFeedback>> questFeedbackMap) {
        return questFeedbackMap.entrySet()
                .stream()
                .map(map -> WeeklySummaryResponse.of(
                        map.getKey(),
                        map.getValue(),
                        map.getValue().stream()
                                .filter(questFeedback -> questFeedback.getQuest().getQuestType() != SPECIAL)
                                .count()
                ))
                .collect(Collectors.toList());
    }

    private static Long getCompletedAllQuestsDateCount(final List<CompletedQuestsCountElement> completedQuestsCountElements) {
        return completedQuestsCountElements.stream()
                .filter(element -> element.getByTypeCount() == 3)
                .count();
    }

    private static Set<String> getHighestAverageCompletionDays(final List<CompletedQuestsCountElement> completedQuestsCountDateElements) {
        if (completedQuestsCountDateElements.isEmpty()) {
            return Collections.emptySet();
        }

        final Map<String, Double> completionCounts = completedQuestsCountDateElements.stream()
                .collect(Collectors.groupingBy(
                        element -> element.getCompletedDate().getDayOfWeek().getDisplayName(SHORT, KOREAN),
                        Collectors.summingDouble(element -> element.getByTypeCount() / 3.0) // double 사용
                ));

        final Double maxCount = Collections.max(completionCounts.values());

        return maxCount == 0 ? Collections.emptySet() : completionCounts.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCount))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private static Long getAverageCompletion(final List<CompletedQuestsCountElement> completedQuestsCountDateElements, final YearMonth yearMonth) {
        final Long totalCount = completedQuestsCountDateElements.stream()
                .mapToLong(element -> element.getByTypeCount())
                .sum();

        final int month = yearMonth.lengthOfMonth();
        return (totalCount * 100) / (month * 3);
    }
}
