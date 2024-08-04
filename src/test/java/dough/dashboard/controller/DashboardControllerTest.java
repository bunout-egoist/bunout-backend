package dough.dashboard.controller;

import dough.global.AbstractControllerTest;
import dough.member.dto.request.MemberInfoRequest;
import dough.quest.dto.response.CompletedQuestDetailResponse;
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
import static java.util.TimeZone.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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

    @DisplayName("달성한 퀘스트의 상세 정보를 조회할 수 있다.")
    @Test
    void getCompletedQuestDetail() throws Exception {
        // given
        final List<CompletedQuestDetailResponse> expectResponses = COMPLETED_QUEST_DETAILS.stream()
                .map(completedQuestDetail ->
                        CompletedQuestDetailResponse.of(
                                completedQuestDetail.quest,
                                completedQuestDetail.feedback
                        )).toList();

        when(questService.getCompletedQuestDetail(anyLong(), any()))
                .thenReturn(expectResponses);

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
                                        .type(LONG)
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
                                        .type(LONG)
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
    void getCompletedQuestDetail_InvalidLocalDateType() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/quests/{memberId}/{searchDate}", 1, "2024-07"))
                .andExpect(status().isBadRequest());
    }
}
