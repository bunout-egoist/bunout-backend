package dough.quest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.FixedQuestResponse;
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

import java.util.List;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
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
                                        .type(NUMBER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
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
                                        .type(NUMBER)
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
                                        .type(NUMBER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("퀘스트를 삭제할 수 있다.")
    @Test
    void deleteQuest() throws Exception {
        // given
        doNothing().when(questService).delete(anyLong());

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/quests/{questId}", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("questId")
                                        .description("퀘스트 아이디")
                        )
                ));
    }

    @DisplayName("번아웃 유형에 해당하는 고정퀘스트를 조회할 수 있다.")
    @Test
    void getFixedQuests() throws Exception {
        // given
        final List<FixedQuestResponse> fixedQuestResponses = List.of(FixedQuestResponse.of(FIXED_QUEST1));

        when(questService.getFixedQuests(anyLong()))
                .thenReturn(fixedQuestResponses);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/quests/fixed/{burnoutId}", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("burnoutId")
                                        .description("번아웃 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[0].id")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[0].description")
                                        .type(STRING)
                                        .description("고정 퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("[0].activity")
                                        .type(STRING)
                                        .description("고정 퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }
}
