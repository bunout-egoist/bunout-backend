package dough.dashboard.service;

import dough.dashboard.domain.repository.DashboardRepository;
import dough.dashboard.dto.CompletedQuestCountElement;
import dough.dashboard.dto.response.TotalCompletedQuestCountResponse;
import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final DashboardRepository dashboardRepository;

    public TotalCompletedQuestCountResponse getTotalCompletedQuestCount(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final List<CompletedQuestCountElement> elements = dashboardRepository.getDashboardByMemberIdAndDate(member.getId());

        return TotalCompletedQuestCountResponse.of(elements);
    }
}
