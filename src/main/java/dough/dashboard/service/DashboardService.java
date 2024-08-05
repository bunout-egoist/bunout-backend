package dough.dashboard.service;

import dough.dashboard.dto.response.DashboardResponse;
import dough.dashboard.dto.response.TotalCompletedQuestCountResponse;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedQuestCountElement;
import dough.quest.dto.DateCompletedQuestCountElement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final QuestRepository questRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final MemberRepository memberRepository;

    public DashboardResponse getDashBoard(final Long memberId, final Long year, final Long month) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final List<DateCompletedQuestCountElement> dateCompletedQuestCountElements = selectedQuestRepository.getDateAndCompletedQuestsCountByMemberId(memberId, year, month);

        return DashboardResponse.of(dateCompletedQuestCountElements);
    }

    public TotalCompletedQuestCountResponse getTotalCompletedQuestCount(final Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final CompletedQuestCountElement completedQuestCountElement = selectedQuestRepository.countTotalCompletedQuestsByMemberId(memberId);
        return TotalCompletedQuestCountResponse.of(completedQuestCountElement.getDailyAndFixedCount(), completedQuestCountElement.getSpecialCount());
    }
}
