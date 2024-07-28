package dough.quest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.QuestResponse;
import dough.quest.service.QuestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
import static java.util.TimeZone.LONG;
import static javax.management.openmbean.SimpleType.INTEGER;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class QuestControllerTest extends AbstractControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private QuestService questService;

    private ResultActions performPutUpdateQuestRequest(
            final Long questId,
            final QuestUpdateRequest questUpdateRequest)
            throws Exception {
        return mockMvc.perform(put("/api/v1/quests/{questId}", questId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questUpdateRequest)));
    }

    @DisplayName("퀘스트를 추가할 수 있다.")
    @Test
    void createQuest() throws Exception {
        // given
        final QuestRequest questRequest = new QuestRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기",
                "데일리",
                3
        );

        when(questService.save(any()))
                .thenReturn(QuestResponse.of(DAILY_QUEST1));

        // when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/quests")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questRequest)));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("description")
                                        .type(STRING)
                                        .description("퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("activity")
                                        .type(STRING)
                                        .description("퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (데일리/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty")
                                        .type(INTEGER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(LONG)
                                        .description("퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("description")
                                        .type(STRING)
                                        .description("퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("activity")
                                        .type(STRING)
                                        .description("퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (데일리/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty")
                                        .type(INTEGER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("퀘스트를 수정할 수 있다.")
    @Test
    void updateQuest() throws Exception {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "20분 운동하기",
                "스페셜",
                4
        );

        doNothing().when(questService).update(anyLong(), any());

        // when
        final ResultActions resultActions = performPutUpdateQuestRequest(DAILY_QUEST1.getId(), questUpdateRequest);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("questId")
                                        .description("퀘스트 아이디")
                        ),
                        requestFields(
                                fieldWithPath("description")
                                        .type(STRING)
                                        .description("퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("activity")
                                        .type(STRING)
                                        .description("퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (데일리/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty")
                                        .type(INTEGER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }
}
