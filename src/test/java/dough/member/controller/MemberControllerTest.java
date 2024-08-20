package dough.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberAttendanceResponse;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MemberControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "accessToken";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getMemberIdFromToken(any()))
                .willReturn(1L);
    }

    private ResultActions performPutUpdateMemberInfoRequest(
            final Long memberId,
            final MemberInfoRequest memberInfoRequest
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/members/{memberId}", memberId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberInfoRequest)));
    }

    private ResultActions performPutUpdateBurnout(
            final Long memberId,
            final BurnoutRequest burnoutRequest
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/members/{memberId}/burnout", memberId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(burnoutRequest)));
    }

    private ResultActions performPutUpdateFixedQuest(
            final Long memberId,
            final FixedQuestRequest fixedQuestRequest
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/members/{memberId}/fixed", memberId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fixedQuestRequest)));
    }

    @DisplayName("멤버의 정보을 조회할 수 있다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(1L, "goeun", 1L, 1L, 2);

        when(memberService.getMemberInfo())
                .thenReturn(memberInfoResponse);

        final ResultActions resultActions = mockMvc.perform(get("/api/v1/members")
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("멤버 아이디")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("burnoutId")
                                        .type(NUMBER)
                                        .description("번아웃 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("fixedQuestId")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("level")
                                        .type(NUMBER)
                                        .description("레벨")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));

        // then
        verify(memberService).getMemberInfo();
    }

    @DisplayName("멤버 닉네임을 수정할 수 있다.")
    @Test
    void updateMemberInfo() throws Exception {
        // given
        final Long id = 1L;
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("minju");
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(id, "goeun", 1L, 1L, 2);

        when(memberService.updateMemberInfo(anyLong(), any()))
                .thenReturn(memberInfoResponse);

        final ResultActions resultActions = performPutUpdateMemberInfoRequest(id, memberInfoRequest);

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("멤버 아이디")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("burnoutId")
                                        .type(NUMBER)
                                        .description("번아웃 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("fixedQuestId")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("level")
                                        .type(NUMBER)
                                        .description("레벨")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));

        // then
        verify(memberService).updateMemberInfo(anyLong(), any());
    }

    @DisplayName("닉네임이 5자를 초과할 경우 예외가 발생한다.")
    @Test
    void updateMemberInfo_NicknameSizeInvalid() throws Exception {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("jjanggu");

        // when
        final ResultActions resultActions = performPutUpdateMemberInfoRequest(1L, memberInfoRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 5자를 초과할 수 없습니다."));
    }

    @DisplayName("멤버의 번아웃 유형을 수정할 수 있다.")
    @Test
    void updateBurnout() throws Exception {
        // given
        final Long id = 1L;
        final BurnoutRequest burnoutRequest = new BurnoutRequest(1L);

        doNothing().when(memberService).updateBurnout(anyLong(), any());

        // when
        final ResultActions resultActions = performPutUpdateBurnout(id, burnoutRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("burnoutId")
                                        .type(NUMBER)
                                        .description("번아웃 아이디")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("번아웃 유형이 null일 경우 예외가 발생한다.")
    @Test
    void updateBurnout_BurnoutNull() throws Exception {
        // given
        final Long id = 1L;
        final BurnoutRequest burnoutRequest = new BurnoutRequest(null);

        doNothing().when(memberService).updateBurnout(anyLong(), any());

        // when
        final ResultActions resultActions = performPutUpdateBurnout(id, burnoutRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("번아웃 아이디를 입력해주세요."));
    }

    @DisplayName("멤버의 고정 퀘스트를 재설정할 수 있다.")
    @Test
    void updateFixedQuest() throws Exception {
        // given
        final Long id = 1L;
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(FIXED_QUEST1.getId());

        doNothing().when(memberService).updateFixedQuest(anyLong(), any());

        // when
        final ResultActions resultActions = performPutUpdateFixedQuest(id, fixedQuestRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("fixedQuestId")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }

    @DisplayName("멤버의 고정 퀘스트를 재설정할 수 있다.")
    @Test
    void updateFixedQuest_FixedQuestNull() throws Exception {
        // given
        final Long id = 1L;
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(null);

        doNothing().when(memberService).updateFixedQuest(anyLong(), any());

        // when
        final ResultActions resultActions = performPutUpdateFixedQuest(id, fixedQuestRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("고정 퀘스트 아이디를 입력해주세요."));
    }

    @DisplayName("멤버의 고정 퀘스트를 재설정할 수 있다.")
    @Test
    void checkAttendance() throws Exception {
        // given
        final Long id = 1L;
        final MemberAttendanceResponse memberAttendanceResponse = new MemberAttendanceResponse(50, 1, 2, true, 5);

        when(memberService.checkAttendance(anyLong()))
                .thenReturn(memberAttendanceResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(put("/api/v1/members/{memberId}/attendance", id));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("exp")
                                        .type(NUMBER)
                                        .description("경험치")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("previousLevel")
                                        .type(NUMBER)
                                        .description("이전 레벨")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("currentLevel")
                                        .type(NUMBER)
                                        .description("현재 레벨")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("isLevelUp")
                                        .type(BOOLEAN)
                                        .description("레벨업 유무")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("attendanceCount")
                                        .type(NUMBER)
                                        .description("출석 일수")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }
}
