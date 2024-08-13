package dough.quest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.keyword.KeywordCode;
import dough.quest.domain.SelectedQuest;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.FixedQuestResponse;
import dough.quest.dto.response.QuestResponse;
import dough.quest.dto.response.TodayQuestListResponse;
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
import static dough.keyword.domain.type.ParticipationType.ALONE;
import static dough.keyword.domain.type.PlaceType.ANYWHERE;
import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
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

    @DisplayName("오늘의 퀘스트를 받을 수 있다.")
    @Test
    void updateTodayQuests() throws Exception {
        // given
        final List<SelectedQuest> todayQuests = List.of(IN_PROGRESS_QUEST1, IN_PROGRESS_QUEST2);
        final TodayQuestListResponse todayQuestListResponse = TodayQuestListResponse.of(new KeywordCode(ANYWHERE.getCode(), ALONE.getCode()), todayQuests);

        when(questService.updateTodayQuests(anyLong()))
                .thenReturn(todayQuestListResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/quests/today/{memberId}", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("placeKeyword")
                                        .type(STRING)
                                        .description("장소 키워드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("participationKeyword")
                                        .type(STRING)
                                        .description("누구와 키워드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests")
                                        .type(ARRAY)
                                        .description("오늘 퀘스트")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("todayQuests[0].activity")
                                        .type(STRING)
                                        .description("고정 퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests[0].description")
                                        .type(STRING)
                                        .description("고정 퀘스트 설명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests[1].activity")
                                        .type(STRING)
                                        .description("고정 퀘스트 활동 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests[1].description")
                                        .type(STRING)
                                        .description("고정 퀘스트 설명")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }
}
