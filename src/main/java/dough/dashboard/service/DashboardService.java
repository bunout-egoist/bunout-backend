package dough.dashboard.service;

import dough.dashboard.domain.Dashboard;
import dough.dashboard.domain.repository.DashboardRepository;
import dough.dashboard.dto.response.DashboardResponse;
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

    public DashboardResponse getDashboard(final Long memberId, final Long year, final Long month) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final List<Dashboard> dashboards = dashboardRepository.getDashboardByMemberIdAndDate(member.getId(), year, month);

        return DashboardResponse.of(dashboards);
    }
}
