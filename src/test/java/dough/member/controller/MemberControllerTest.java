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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MemberControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "Bearer accessToken";

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

    private ResultActions performPutUpdateMemberInfoRequest(final MemberInfoRequest memberInfoRequest) throws Exception {
        return mockMvc.perform(put("/api/v1/members")
                .header(AUTHORIZATION, MEMBER_TOKENS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberInfoRequest)));
    }

    private ResultActions performPutUpdateBurnout(final BurnoutRequest burnoutRequest) throws Exception {
        return mockMvc.perform(put("/api/v1/members/burnout")
                .header(AUTHORIZATION, MEMBER_TOKENS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(burnoutRequest)));
    }

    private ResultActions performPutUpdateFixedQuest(final FixedQuestRequest fixedQuestRequest) throws Exception {
        return mockMvc.perform(put("/api/v1/members/fixed")
                .header(AUTHORIZATION, MEMBER_TOKENS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fixedQuestRequest)));
    }

    @DisplayName("멤버의 정보를 조회할 수 있다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                1L,
                "goeun",
                "소보로",
                1L,
                2
        );

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
                                fieldWithPath("burnoutName")
                                        .type(STRING)
                                        .description("번아웃 유형 이름")
                                        .attributes(field("constraint", "문자열")),
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
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("minju");
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                1L,
                "goeun",
                "소보로",
                1L,
                2
        );

        when(memberService.updateMemberInfo(any()))
                .thenReturn(memberInfoResponse);

        final ResultActions resultActions = performPutUpdateMemberInfoRequest(memberInfoRequest);

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
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
                                fieldWithPath("burnoutName")
                                        .type(STRING)
                                        .description("번아웃 유형 이름")
                                        .attributes(field("constraint", "문자열")),
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
        verify(memberService).updateMemberInfo(any());
    }

    @DisplayName("닉네임이 5자를 초과할 경우 예외가 발생한다.")
    @Test
    void updateMemberInfo_NicknameSizeInvalid() throws Exception {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("jjanggu");

        // when
        final ResultActions resultActions = performPutUpdateMemberInfoRequest(memberInfoRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임은 5자를 초과할 수 없습니다."));
    }

    @DisplayName("멤버의 번아웃 유형을 수정할 수 있다.")
    @Test
    void updateBurnout() throws Exception {
        // given
        final BurnoutRequest burnoutRequest = new BurnoutRequest(1L);

        doNothing().when(memberService).updateBurnout(any());

        // when
        final ResultActions resultActions = performPutUpdateBurnout(burnoutRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
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
        final BurnoutRequest burnoutRequest = new BurnoutRequest(null);

        doNothing().when(memberService).updateBurnout(any());

        // when
        final ResultActions resultActions = performPutUpdateBurnout(burnoutRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("번아웃 아이디를 입력해주세요."));
    }

    @DisplayName("멤버의 고정 퀘스트를 재설정할 수 있다.")
    @Test
    void updateFixedQuest() throws Exception {
        // given
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(FIXED_QUEST1.getId());

        doNothing().when(memberService).updateFixedQuest(any());

        // when
        final ResultActions resultActions = performPutUpdateFixedQuest(fixedQuestRequest);

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
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
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(null);

        doNothing().when(memberService).updateFixedQuest(any());

        // when
        final ResultActions resultActions = performPutUpdateFixedQuest(fixedQuestRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("고정 퀘스트 아이디를 입력해주세요."));
    }

    @DisplayName("멤버는 출석 체크를 할 수 있다.")
    @Test
    void checkAttendance() throws Exception {
        // given
        final MemberAttendanceResponse memberAttendanceResponse = new MemberAttendanceResponse(
                1,
                2,
                45,
                50,
                false,
                5
        );

        when(memberService.checkAttendance())
                .thenReturn(memberAttendanceResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(put("/api/v1/members/attendance")
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("currentLevel")
                                        .type(NUMBER)
                                        .description("현재 레벨")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("nextLevel")
                                        .type(NUMBER)
                                        .description("다음 레벨")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("currentExp")
                                        .type(NUMBER)
                                        .description("현재 경험치 (멤버의 Total 경험치 아님)")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("requiredExp")
                                        .type(NUMBER)
                                        .description("다음 레벨까지 필요한 경험치")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("isLevelUp")
                                        .type(BOOLEAN)
                                        .description("레벨업 유무")
                                        .attributes(field("constraint", "불리언")),
                                fieldWithPath("attendanceCount")
                                        .type(NUMBER)
                                        .description("현재 출석 점수")
                                        .attributes(field("constraint", "양의 정수"))
                        )
                ));
    }
}