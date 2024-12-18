package dough.dashboard.service;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.quest.domain.QuestFeedback;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.CompletedQuestsTotalElement;
import dough.quest.dto.response.TotalAndStatisticsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.CompletedQuestElementFixture.QUEST_ELEMENT1;
import static dough.quest.fixture.QuestFixture.BY_TYPE_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Transactional
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @DisplayName("달성한 퀘스트의 상세 정보를 조회할 수 있다.")
    @Test
    void getWeeklySummary() {
        // given
        final Long memberId = 1L;

        final LocalDate date = LocalDate.now();
        final LocalDate startDate = date.minusDays(3);
        final LocalDate endDate = date.plusDays(3);

        given(selectedQuestRepository.findCompletedQuestsByMemberIdAndDate(memberId, startDate, endDate))
                .willReturn(List.of(QUEST_ELEMENT1));

        // when
        final List<WeeklySummaryResponse> actualResponse = dashboardService.getWeeklySummary(GOEUN.getId(), LocalDate.now());

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(List.of(WeeklySummaryResponse.of(LocalDate.of(2024, 8, 11), List.of(new QuestFeedback(BY_TYPE_QUEST1, "https://~")), 1L)));
    }

    @DisplayName("스페셜 퀘스트와 데일리 퀘스트의 총합과 통계를 조회할 수 있다.")
    @Test
    void getCompletedQuestsTotalAndStatistics() {
        // given
        final CompletedQuestsTotalElement completedQuestsTotalElement = new CompletedQuestsTotalElement(50L, 40L);

        given(selectedQuestRepository.getCompletedQuestsTotalByMemberId(any()))
                .willReturn(completedQuestsTotalElement);
        given(selectedQuestRepository.getCompletedQuestsCountByMemberIdAndDate(anyLong(), anyInt(), anyInt()))
                .willReturn(List.of(new CompletedQuestsCountElement(LocalDate.of(2024, 9, 7), 3L, 1L)));

        // when
        final TotalAndStatisticsResponse actualResponse = dashboardService.getCompletedQuestsTotalAndStatistics(GOEUN.getId());

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(TotalAndStatisticsResponse.of(50L, 40L, Set.of("토"), 3L));
    }

    @DisplayName("월간 분석을 받을 수 있다.")
    @Test
    void getMonthlyDashboard() {
        // given
        given(selectedQuestRepository.getCompletedQuestsCountByMemberIdAndDate(anyLong(), anyInt(), anyInt()))
                .willReturn(List.of(new CompletedQuestsCountElement(LocalDate.now(), 10L, 10L)));

        // when
        final MonthlySummaryResponse actualResponse = dashboardService.getMonthlySummary(GOEUN.getId(), YearMonth.of(2024, 8));

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(MonthlySummaryResponse.of(
                        List.of(new CompletedQuestsCountElement(LocalDate.now(), 10L, 10L)),
                        0L
                ));
    }
}
