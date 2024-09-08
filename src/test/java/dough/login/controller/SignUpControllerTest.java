package dough.login.controller;

import dough.global.AbstractControllerTest;
import dough.login.dto.request.SignUpRequest;
import dough.login.service.SignUpService;
import dough.member.dto.response.MemberInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignUpController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc
public class SignUpControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "Bearer accessToken";

    @MockBean
    private SignUpService signUpService;

    private MemberInfoResponse memberInfoResponse;

    private String validAccessToken;

    @BeforeEach
    public void setup() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getMemberIdFromToken(any()))
                .willReturn(1L);
        validAccessToken = "validAccessToken";
        memberInfoResponse = new MemberInfoResponse(1L, "goeun", "소보로", 1L, 2);
    }

    @DisplayName("유효한 토큰이 있을경우 회원 정보를 업데이트할 수 있습니다.")
    @Test
    @WithMockUser
    public void testSignupInfo_withValidToken() throws Exception {
        // Given
        Mockito.when(tokenProvider.validToken(anyString())).thenReturn(true);
        Mockito.when(signUpService.updateMemberInfo(any(SignUpRequest.class))).thenReturn(memberInfoResponse);

        // When
        mockMvc.perform(post("/api/v1/signup/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 토큰 추가
                        .content("{ \"accessToken\": \"" + validAccessToken + "\", \"nickname\": \"nick\", \"gender\": \"남성\", \"birth_year\": 1990, \"occupation\": \"직장인\" }"))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberInfoResponse.getId()))
                .andExpect(jsonPath("$.nickname").value(memberInfoResponse.getNickname()));
    }

    @DisplayName("유효하지 않은 토큰이 있을 경우 401에러를 반환합니다.")
    @Test()
    @WithMockUser
    public void testSignupInfo_withInvalidToken() throws Exception {
        // Given
        Mockito.when(tokenProvider.validToken(anyString())).thenReturn(false);

        // When
        mockMvc.perform(post("/api/v1/signup/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 토큰 추가
                        .content("{ \"accessToken\": \"invalidAccessToken\", \"nickname\": \"nick\", \"gender\": \"남성\", \"birth_year\": 1990, \"occupation\": \"직장인\" }"))

                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid Token"));
    }

    @DisplayName("멤버는 탈퇴를 할 수 있다.")
    @Test
    void signout() throws Exception {
        // given & when
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
