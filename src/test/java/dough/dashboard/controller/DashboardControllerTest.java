package dough.dashboard.controller;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.dashboard.service.DashboardService;
import dough.global.AbstractControllerTest;
import dough.quest.domain.QuestFeedback;
import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.response.TotalAndStatisticsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static dough.quest.fixture.QuestFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class DashboardControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "Bearer accessToken";

    @MockBean
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getSubject(any()))
                .willReturn("1");
    }

    private ResultActions performGetMonthlySummaryRequest() throws Exception {
        return mockMvc.perform(get("/api/v1/dashboard/monthly/{yearMonth}",
                YearMonth.of(2024, 8))
                .header(AUTHORIZATION, MEMBER_TOKENS));
    }

    @DisplayName("주간 분석을 받을 수 있다.")
    @Test
    void getWeeklySummary() throws Exception {
        // given
        final List<WeeklySummaryResponse> actualResponse = List.of(
                WeeklySummaryResponse.of(LocalDate.of(2024, 8, 11), List.of(new QuestFeedback(BY_TYPE_QUEST1, "https://~"), new QuestFeedback(BY_TYPE_QUEST2, "https://~")), 2L),
                WeeklySummaryResponse.of(LocalDate.of(2024, 8, 14), List.of(new QuestFeedback(FIXED_QUEST1, "https://~")), 1L)
        );

        when(dashboardService.getWeeklySummary(anyLong(), any()))
                .thenReturn(actualResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                get("/api/v1/dashboard/weekly/{searchDate}", LocalDate.of(2024, 8, 13))
                        .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("searchDate")
                                        .description("조회 날짜")
                        ),
                        responseFields(
                                fieldWithPath("[0].completedDate")
                                        .type(STRING)
                                        .description("완료 날짜 (yyyy-MM-dd)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].questDetails[0].imageUrl")
                                        .type(STRING)
                                        .description("피드백 이미지 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].questDetails[0].activity")
                                        .type(STRING)
                                        .description("퀘스트 활동")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].questDetails[0].description")
                                        .type(STRING)
                                        .description("퀘스트 상세 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].questDetails[0].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (고정/유형별/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].questDetails[1].imageUrl")
                                        .type(STRING)
                                        .description("피드백 이미지 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].questDetails[1].activity")
                                        .type(STRING)
                                        .description("퀘스트 활동")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].questDetails[1].description")
                                        .type(STRING)
                                        .description("퀘스트 상세 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].questDetails[1].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (고정/유형별/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].dailyCount")
                                        .type(NUMBER)
                                        .description("완료한 데일리 퀘스트 개수")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[1].completedDate")
                                        .type(STRING)
                                        .description("완료 날짜 (yyyy-MM-dd)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].questDetails[0].imageUrl")
                                        .type(STRING)
                                        .description("피드백 이미지 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].questDetails[0].activity")
                                        .type(STRING)
                                        .description("퀘스트 활동")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].questDetails[0].description")
                                        .type(STRING)
                                        .description("퀘스트 상세 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].questDetails[0].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (고정/유형별/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[1].dailyCount")
                                        .type(NUMBER)
                                        .description("완료한 데일리 퀘스트 개수")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("조회 날짜 타입이 맞지 않을 경우 예외가 발생한다.")
    @Test
    void getWeeklySummary_InvalidLocalDateType() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/weekly/{searchDate}", "2024-07")
                        .header(AUTHORIZATION, MEMBER_TOKENS))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("스페셜 퀘스트와 데일리 퀘스트의 총합과 통계를 조회할 수 있다.")
    @Test
    void getCompletedQuestsTotalAndStatistics() throws Exception {
        // given
        final TotalAndStatisticsResponse totalResponse = TotalAndStatisticsResponse.of(
                50L,
                40L,
                Set.of("화"),
                19L);

        when(dashboardService.getCompletedQuestsTotalAndStatistics(anyLong()))
                .thenReturn(totalResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                get("/api/v1/dashboard/total", LocalDate.now())
                        .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("dailyTotal")
                                        .type(NUMBER)
                                        .description("완료한 데일리 퀘스트 총합")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("specialTotal")
                                        .type(NUMBER)
                                        .description("완료한 스페셜 퀘스트 총합")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("highestAverageCompletionDay")
                                        .type(ARRAY)
                                        .description("평균 달성률이 가장 높은 요일 배열")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("averageCompletion")
                                        .type(NUMBER)
                                        .description("이번 달 평균 달성률")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("월간 분석을 받을 수 있다.")
    @Test
    void getMonthlySummary() throws Exception {
        // given
        final MonthlySummaryResponse monthlySummaryResponse = MonthlySummaryResponse.of(
                List.of(new CompletedQuestsCountElement(LocalDate.now(), 10L, 10L)),
                0L
        );

        when(dashboardService.getMonthlySummary(anyLong(), any()))
                .thenReturn(monthlySummaryResponse);

        // when
        final ResultActions resultActions = performGetMonthlySummaryRequest();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("yearMonth")
                                        .description("연월")
                        ),
                        responseFields(
                                fieldWithPath("countDetails")
                                        .type(ARRAY)
                                        .description("완료 퀘스트 개수 & 완료 날짜 리스트")
                                        .attributes(field("constraint", "배열")),
                                fieldWithPath("countDetails[].completedDate")
                                        .type(STRING)
                                        .description("퀘스트 완료 날짜")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("countDetails[].dailyCount")
                                        .type(NUMBER)
                                        .description("완료한 데일리 퀘스트 개수")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("completedAllQuestsDateCount")
                                        .type(NUMBER)
                                        .description("하루에 제공되는 모든 퀘스트 완료 일수")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("월간 분석을 제공하기 위한 사용자의 기록이 없을 수 있다.")
    @Test
    void getMonthlySummary_NoSummary() throws Exception {
        // given
        final MonthlySummaryResponse monthlySummaryResponse = MonthlySummaryResponse.of(
                List.of(),
                0L
        );

        when(dashboardService.getMonthlySummary(anyLong(), any()))
                .thenReturn(monthlySummaryResponse);

        // when
        final ResultActions resultActions = performGetMonthlySummaryRequest();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("yearMonth")
                                        .description("연월 (yyyy-MM)")
                        ),
                        responseFields(
                                fieldWithPath("countDetails")
                                        .type(ARRAY)
                                        .description("완료 퀘스트 개수 & 완료 날짜 리스트")
                                        .attributes(field("constraint", "배열")),
                                fieldWithPath("completedAllQuestsDateCount")
                                        .type(NUMBER)
                                        .description("하루에 제공되는 모든 퀘스트 완료 일수")
                                        .attributes(field("constraint", "양의 정수"))

                        )
                ));
    }
}
