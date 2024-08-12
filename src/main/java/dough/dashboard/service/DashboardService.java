package dough.dashboard.service;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.TotalCompletedQuestsElement;
import dough.quest.dto.response.TotalCompletedQuestsResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final SelectedQuestRepository selectedQuestRepository;
    private final MemberRepository memberRepository;

    public MonthlySummaryResponse getMonthlySummary(final Long memberId, final YearMonth yearMonth) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final List<CompletedQuestsCountElement> completedQuestsCountElements = selectedQuestRepository.getCompletedQuestsCountByMemberIdAndDate(memberId, yearMonth);
        final Long completedAllQuestsDateCount = getCompletedAllQuestsDateCount(completedQuestsCountElements);
        final Set<String> highestAverageCompletionDays = getHighestAverageCompletionDays(completedQuestsCountElements);
        final Long averageCompletion = getAverageCompletion(completedQuestsCountElements);

        return MonthlySummaryResponse.of(
                completedQuestsCountElements,
                completedAllQuestsDateCount,
                highestAverageCompletionDays,
                averageCompletion
        );
    }

    public TotalCompletedQuestsResponse getTotalCompletedQuests(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final TotalCompletedQuestsElement totalCompletedQuestsElement = selectedQuestRepository.getTotalCompletedQuestsByMemberId(memberId);

        return TotalCompletedQuestsResponse.of(
                totalCompletedQuestsElement.getDailyAndFixedTotal(),
                totalCompletedQuestsElement.getSpecialTotal()
        );
    }

    private static Long getCompletedAllQuestsDateCount(final List<CompletedQuestsCountElement> completedQuestsCountElements) {
        return completedQuestsCountElements.stream()
                .filter(element -> element.getDailyAndFixedCount() == 3)
                .count();
    }

    private static Long getAverageCompletion(final List<CompletedQuestsCountElement> completedQuestsCountDateElements) {
        final Long totalCount = completedQuestsCountDateElements.stream()
                .mapToLong(element -> element.getDailyAndFixedCount())
                .sum();

        final int month = completedQuestsCountDateElements.get(0).getCompletedDate().lengthOfMonth();
        return (totalCount * 100) / (month * 3);
    }

    private static Set<String> getHighestAverageCompletionDays(final List<CompletedQuestsCountElement> completedQuestsCountDateElements) {
        final Map<String, Long> completionCounts = completedQuestsCountDateElements.stream()
                .collect(Collectors.groupingBy(
                        element -> element.getCompletedDate().getDayOfWeek().getDisplayName(SHORT, KOREAN),
                        Collectors.summingLong(element -> (element.getDailyAndFixedCount() / 3)
                )));

        final Long maxCount = Collections.max(completionCounts.values());

        return maxCount == 0 ? null : completionCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .keySet();
    }
}
