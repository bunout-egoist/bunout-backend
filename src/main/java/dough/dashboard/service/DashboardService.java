package dough.dashboard.service;

import dough.dashboard.dto.response.DashboardResponse;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedCountDateElement;
import dough.quest.dto.TotalCompletedQuestsElement;
import dough.quest.dto.response.TotalCompletedQuestsResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private static Long getCompletedAllQuestsCount(final List<CompletedCountDateElement> completedCountDateElements) {
        return completedCountDateElements.stream()
                .filter(element -> element.getDailyAndFixedCount() == 3)
                .count();
    }

    private static Long getAverageCompletion(final List<CompletedCountDateElement> completedCountDateElements) {
        final Long totalCount = completedCountDateElements.stream()
                .mapToLong(element -> element.getDailyAndFixedCount() + element.getSpecialCount())
                .sum();

        final int month = completedCountDateElements.get(0).getCompletedAt().getDayOfMonth();
        return totalCount / (month * 3 + 12);
    }

    private static Map<String, Long> getHighestAverageCompletionDays(final List<CompletedCountDateElement> completedCountDateElements) {
        final Map<String, Long> completionCounts = completedCountDateElements.stream()
                .collect(Collectors.groupingBy(
                        element -> element.getCompletedAt().getDayOfWeek().getDisplayName(SHORT, KOREAN),
                        Collectors.summingLong(element -> element.getSpecialCount() + element.getDailyAndFixedCount())
                ));

        final Long maxCount = Collections.max(completionCounts.values());

        return maxCount == 0 ? null : completionCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public DashboardResponse getMonthlyDashboard(final Long memberId, final Long year, final Long month) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final List<CompletedCountDateElement> completedCountDateElements = selectedQuestRepository.getDateAndCompletedQuestsCountByMemberId(memberId, year, month);
        final Long completedAllQuestsCount = getCompletedAllQuestsCount(completedCountDateElements);
        final Map<String, Long> highestAverageCompletionDays = getHighestAverageCompletionDays(completedCountDateElements);
        final Long averageCompletion = getAverageCompletion(completedCountDateElements);

        return DashboardResponse.of(
                completedCountDateElements,
                completedAllQuestsCount,
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
}
