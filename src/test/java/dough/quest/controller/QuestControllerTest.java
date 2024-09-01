package dough.quest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.keyword.KeywordCode;
import dough.login.service.TokenService;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.SelectedQuest;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.FixedQuestListResponse;
import dough.quest.dto.response.TodayQuestListResponse;
import dough.quest.service.QuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.SOBORO;
import static dough.global.restdocs.RestDocsConfiguration.field;
import static dough.keyword.domain.type.ParticipationType.ALONE;
import static dough.keyword.domain.type.PlaceType.ANYWHERE;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.QuestFixture.*;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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

    private static final String MEMBER_TOKENS = "Bearer accessToken";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private QuestService questService;

    @Mock
    private TokenService tokenService;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getMemberIdFromToken(any()))
                .willReturn(1L);
    }

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
                "점심시간, 몸과 마음을 건강하게 유지하며 15분 운동하기",
                "유형별",
                3,
                true,
                false,
                "소보로"
        );

        doNothing().when(questService).save(any());

        // when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/quests")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questRequest)));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("content")
                                        .type(STRING)
                                        .description("퀘스트 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (고정/유형별/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty")
                                        .type(NUMBER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("isOutside")
                                        .type(BOOLEAN)
                                        .description("퀘스트가 밖에서 진행되는지 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("isGroup")
                                        .type(BOOLEAN)
                                        .description("퀘스트가 다른 사람과 함께 수행되는지 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("burnoutName")
                                        .type(STRING)
                                        .description("번아웃 이름")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

    @DisplayName("퀘스트를 수정할 수 있다.")
    @Test
    void updateQuest() throws Exception {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며 20분 운동하기",
                "스페셜",
                4,
                false,
                false,
                "소보로"
        );

        doNothing().when(questService).update(anyLong(), any());

        // when
        final ResultActions resultActions = performPutUpdateQuestRequest(BY_TYPE_QUEST1.getId(), questUpdateRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("questId")
                                        .description("퀘스트 아이디")
                        ),
                        requestFields(
                                fieldWithPath("content")
                                        .type(STRING)
                                        .description("퀘스트 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("questType")
                                        .type(STRING)
                                        .description("퀘스트 타입 (고정/유형별/스페셜)")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty")
                                        .type(NUMBER)
                                        .description("난이도")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("isOutside")
                                        .type(BOOLEAN)
                                        .description("퀘스트가 밖에서 진행되는지 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("isGroup")
                                        .type(BOOLEAN)
                                        .description("퀘스트가 다른 사람과 함께 수행되는지 여부")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("burnoutName")
                                        .type(STRING)
                                        .description("번아웃 이름")
                                        .attributes(field("constraint", "문자열"))
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
        resultActions.andExpect(status().isNoContent())
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
        final FixedQuestListResponse fixedQuestListResponse = FixedQuestListResponse.of(
                SOBORO, List.of(FIXED_QUEST1, FIXED_QUEST2)
        );

        when(questService.getFixedQuests(anyLong()))
                .thenReturn(fixedQuestListResponse);

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
                                fieldWithPath("burnoutName")
                                        .type(STRING)
                                        .description("번아웃 이름")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("fixedQuests[0].questId")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("fixedQuests[0].content")
                                        .type(STRING)
                                        .description("고정 퀘스트 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("fixedQuests[1].questId")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("fixedQuests[1].content")
                                        .type(STRING)
                                        .description("고정 퀘스트 내용")
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

        // given
        given(tokenService.getMemberId())
                .willReturn(1L);
        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.of(GOEUN));
        when(questService.updateTodayQuests())
                .thenReturn(todayQuestListResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/api/v1/quests/today")
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
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
                                fieldWithPath("todayQuests[0].content")
                                        .type(STRING)
                                        .description("퀘스트 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests[0].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests[1].content")
                                        .type(STRING)
                                        .description("퀘스트 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("todayQuests[1].questType")
                                        .type(STRING)
                                        .description("퀘스트 타입")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }
}
