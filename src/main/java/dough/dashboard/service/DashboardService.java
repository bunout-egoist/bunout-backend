package dough.dashboard.service;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.CompletedQuestsTotalElement;
import dough.quest.dto.response.CompletedQuestsTotalResponse;
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

        final int year = yearMonth.getYear();
        final int month = yearMonth.getMonthValue();
        final List<CompletedQuestsCountElement> completedQuestsCountElements = selectedQuestRepository.getCompletedQuestsCountByMemberIdAndDate(memberId, year, month);

        final Long completedAllQuestsDateCount = getCompletedAllQuestsDateCount(completedQuestsCountElements);
        final Set<String> highestAverageCompletionDays = getHighestAverageCompletionDays(completedQuestsCountElements);
        final Long averageCompletion = getAverageCompletion(completedQuestsCountElements, yearMonth);

        return MonthlySummaryResponse.of(
                completedQuestsCountElements,
                completedAllQuestsDateCount,
                highestAverageCompletionDays,
                averageCompletion
        );
    }

    public CompletedQuestsTotalResponse getCompletedQuestsTotal(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final CompletedQuestsTotalElement completedQuestsTotalElement = selectedQuestRepository.getCompletedQuestsTotalByMemberId(memberId);

        return CompletedQuestsTotalResponse.of(
                completedQuestsTotalElement.getDailyTotal(),
                completedQuestsTotalElement.getSpecialTotal()
        );
    }

    private static Long getCompletedAllQuestsDateCount(final List<CompletedQuestsCountElement> completedQuestsCountElements) {
        return completedQuestsCountElements.stream()
                .filter(element -> element.getBY_TYPECount() == 3)
                .count();
    }

    private static Long getAverageCompletion(final List<CompletedQuestsCountElement> completedQuestsCountDateElements, final YearMonth yearMonth) {
        final Long totalCount = completedQuestsCountDateElements.stream()
                .mapToLong(element -> element.getBY_TYPECount())
                .sum();

        final int month = yearMonth.lengthOfMonth();
        return (totalCount * 100) / (month * 3);
    }

    private static Set<String> getHighestAverageCompletionDays(final List<CompletedQuestsCountElement> completedQuestsCountDateElements) {
        if (completedQuestsCountDateElements.isEmpty()) {
            return Collections.emptySet();
        }

        final Map<String, Double> completionCounts = completedQuestsCountDateElements.stream()
                .collect(Collectors.groupingBy(
                        element -> element.getCompletedDate().getDayOfWeek().getDisplayName(SHORT, KOREAN),
                        Collectors.summingDouble(element -> element.getBY_TYPECount() / 3.0) // double 사용
                ));

        final Double maxCount = Collections.max(completionCounts.values());

        return maxCount == 0 ? Collections.emptySet() : completionCounts.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCount))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
