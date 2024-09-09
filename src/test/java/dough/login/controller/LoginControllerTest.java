package dough.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.AbstractControllerTest;
import dough.login.dto.request.SignUpRequest;
import dough.login.service.LoginService;
import dough.member.dto.response.MemberInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc
public class LoginControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "Bearer accessToken";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    private SignUpRequest signUpRequest;

    @BeforeEach
    public void setup() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getMemberIdFromToken(any()))
                .willReturn(1L);

        signUpRequest = new SignUpRequest(
                "nickname",
                "남자",
                2002,
                "기타",
                1L,
                1L
        );
    }

    private ResultActions performPutCompleteSignupRequest(final SignUpRequest signUpRequest) throws Exception {
        return mockMvc.perform(put("/api/v1/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
                .header(AUTHORIZATION, MEMBER_TOKENS));
    }


    @DisplayName("멤버의 추가 회원가입을 진행할 수 있다.")
    @Test
    void completeSignup() throws Exception {
        // Given
        final MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                1L,
                "goeun",
                "소보로",
                1L,
                2
        );

        when(loginService.completeSignup(any(SignUpRequest.class)))
                .thenReturn(memberInfoResponse);

        // when
        final ResultActions resultActions = performPutCompleteSignupRequest(signUpRequest);

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
                                        .attributes(field("constraint", "5자 이내의 문자열")),
                                fieldWithPath("gender")
                                        .type(STRING)
                                        .description("성별")
                                        .attributes(field("constraint", "문자열 (남성, 여성, 기타)")),
                                fieldWithPath("birthYear")
                                        .type(NUMBER)
                                        .description("생일")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("occupation")
                                        .type(STRING)
                                        .description("직업")
                                        .attributes(field("constraint", "문자열 (학생, 직장인, 자영업, 주부, 무직, 기타)")),
                                fieldWithPath("fixedQuestId")
                                        .type(NUMBER)
                                        .description("고정 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("burnoutId")
                                        .type(NUMBER)
                                        .description("번아웃 아이디")
                                        .attributes(field("constraint", "양의 정수"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("멤버 아이디")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "5자 이내의 문자열")),
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
    }

    @DisplayName("멤버는 탈퇴를 할 수 있다.")
    @Test
    void signout() throws Exception {
        // given
        doNothing().when(loginService)
                .signout();

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/signout")
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        )
                ));
    }
}
