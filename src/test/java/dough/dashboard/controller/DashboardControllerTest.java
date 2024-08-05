package dough.dashboard.controller;

import dough.dashboard.service.DashboardService;
import dough.global.AbstractControllerTest;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import dough.quest.dto.response.TotalCompletedQuestsResponse;
import dough.quest.service.QuestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static dough.feedback.fixture.CompletedQuestDetailFixture.COMPLETED_QUEST_DETAILS;
import static dough.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class DashboardControllerTest extends AbstractControllerTest {

    @MockBean
    private QuestService questService;

    @MockBean
    private DashboardService dashboardService;


    @DisplayName("달성한 퀘스트의 상세 정보를 조회할 수 있다.")
    @Test
    void getCompletedQuestsDetail() throws Exception {
        // given
        final List<CompletedQuestDetailResponse> detailResponses = COMPLETED_QUEST_DETAILS.stream()
                .map(completedQuestDetail ->
                        CompletedQuestDetailResponse.of(
                                completedQuestDetail.quest,
                                completedQuestDetail.feedback
                        )).toList();

        when(questService.getCompletedQuestsDetail(anyLong(), any()))
                .thenReturn(detailResponses);

        // when
        final ResultActions resultActions = mockMvc.perform(
                get("/api/v1/dashboard/quests/{memberId}/{searchDate}", 1L, LocalDate.now()));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디"),
                                parameterWithName("searchDate")
                                        .description("조회 날짜")
                        ),
                        responseFields(
                                fieldWithPath("[].id")
                                        .type(NUMBER)
                                        .description("피드백 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].imageUrl")
                                        .type(STRING)
                                        .description("피드백 이미지 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].description")
                                        .type(STRING)
                                        .description("퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].activity")
                                        .type(STRING)
                                        .description("퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (데일리/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].id")
                                        .type(NUMBER)
                                        .description("피드백 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].imageUrl")
                                        .type(STRING)
                                        .description("피드백 이미지 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].description")
                                        .type(STRING)
                                        .description("퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].activity")
                                        .type(STRING)
                                        .description("퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (데일리/스페셜)")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("조회 날짜 타입이 맞지 않을 경우 예외가 발생한다.")
    @Test
    void getCompletedQuestsDetail_InvalidLocalDateType() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/quests/{memberId}/{searchDate}", 1, "2024-07"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("스페셜 퀘스트와 데일리 퀘스트의 총합을 조회할 수 있다.")
    @Test
    void getTotalCompletedQuests() throws Exception {
        // given
        final TotalCompletedQuestsResponse totalResponse = TotalCompletedQuestsResponse.of(50L, 40L);

        when(dashboardService.getTotalCompletedQuests(anyLong()))
                .thenReturn(totalResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                get("/api/v1/dashboard/total/{memberId}", 1L, LocalDate.now()));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("dailyAndFixedCount")
                                        .type(NUMBER)
                                        .description("데일리/고정 퀘스트 개수")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("specialCount")
                                        .type(NUMBER)
                                        .description("스페셜 퀘스트 개수")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }
}
