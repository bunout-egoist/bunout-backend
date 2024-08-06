package dough.dashboard.service;

import dough.dashboard.dto.response.DashboardResponse;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedCountDateElement;
import dough.quest.dto.TotalCompletedQuestsElement;
import dough.quest.dto.response.TotalCompletedQuestsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static dough.member.fixture.MemberFixture.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Transactional
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @DisplayName("스페셜 퀘스트와 데일리 퀘스트의 총합을 조회할 수 있다.")
    @Test
    void getTotalCompletedQuests() {
        // given
        final TotalCompletedQuestsElement totalCompletedQuestsElement = new TotalCompletedQuestsElement(50L, 40L);

        given(memberRepository.existsById(any()))
                .willReturn(true);
        given(selectedQuestRepository.getTotalCompletedQuestsByMemberId(any()))
                .willReturn(totalCompletedQuestsElement);

        // when
        final TotalCompletedQuestsResponse actualResponse = dashboardService.getTotalCompletedQuests(MEMBER.getId());

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(TotalCompletedQuestsResponse.of(50L, 40L));
    }

    @DisplayName("월간 분석을 받을 수 있다.")
    @Test
    void getMonthlyDashboard() {
        // given
        given(memberRepository.existsById(any()))
                .willReturn(true);
        given(selectedQuestRepository.getDateAndCompletedQuestsCountByMemberId(anyLong(), anyLong(), anyLong()))
                .willReturn(List.of(new CompletedCountDateElement(LocalDate.now(), 10L, 10L)));

        // when
        final DashboardResponse actualResponse = dashboardService.getMonthlyDashboard(MEMBER.getId(), 2024L, 8L);

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(DashboardResponse.of(
                        List.of(new CompletedCountDateElement(LocalDate.now(), 10L, 10L)),
                        0L,
                        Set.of("화"),
                        19L
                ));
    }
}
