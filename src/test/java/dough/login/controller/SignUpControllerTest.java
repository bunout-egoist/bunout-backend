package dough.login.controller;

import dough.DoughApplication;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.login.service.SignUpService;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DoughApplication.class)
@AutoConfigureMockMvc
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private SignUpService signUpService;

    private MemberInfoResponse memberInfoResponse;
    private String validAccessToken;

    @BeforeEach
    public void setup() {
        validAccessToken = "validAccessToken";
        memberInfoResponse = new MemberInfoResponse(1L, "nick");
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

    @DisplayName("유효하지 않으 토큰이 있을 경우 401에러를 반환합니다.")
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
}
