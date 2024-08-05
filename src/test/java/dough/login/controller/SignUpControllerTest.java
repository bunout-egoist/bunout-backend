package dough.login.controller;

import dough.DoughApplication;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
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
    private MemberService memberService;

    private SignUpRequest validSignUpRequest;
    private MemberInfoResponse memberInfoResponse;
    private String validAccessToken;

    @BeforeEach
    public void setup() {
        validAccessToken = "validAccessToken";
        validSignUpRequest = new SignUpRequest(validAccessToken, "nick", "남성", 1990, "직장인");
        memberInfoResponse = new MemberInfoResponse(1L, "nick");
    }

    @Test
    @WithMockUser
    public void testSignupInfo_withValidToken() throws Exception {
        // Given: 유효한 토큰이 있고, 회원 가입 요청이 존재하며, 해당 요청에 대한 기대 응답이 정의됨
        Mockito.when(tokenProvider.validToken(anyString())).thenReturn(true);
        Mockito.when(memberService.updateMemberInfo(any(SignUpRequest.class))).thenReturn(memberInfoResponse);

        // When: 유효한 토큰과 함께 회원 가입 정보 업데이트 요청을 보내면
        mockMvc.perform(post("/api/v1/signup/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 토큰 추가
                        .content("{ \"accessToken\": \"" + validAccessToken + "\", \"nickname\": \"nick\", \"gender\": \"남성\", \"birth_year\": 1990, \"occupation\": \"직장인\" }"))

                // Then: HTTP 200 OK 응답을 받고, 응답 내용이 기대하는 회원 정보와 일치해야 함
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberInfoResponse.getId()))
                .andExpect(jsonPath("$.nickname").value(memberInfoResponse.getNickname()));
    }

    @Test
    @WithMockUser
    public void testSignupInfo_withInvalidToken() throws Exception {
        // Given: 유효하지 않은 토큰이 있음
        Mockito.when(tokenProvider.validToken(anyString())).thenReturn(false);

        // When: 유효하지 않은 토큰과 함께 회원 가입 정보 업데이트 요청을 보내면
        mockMvc.perform(post("/api/v1/signup/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 토큰 추가
                        .content("{ \"accessToken\": \"invalidAccessToken\", \"nickname\": \"nick\", \"gender\": \"남성\", \"birth_year\": 1990, \"occupation\": \"직장인\" }"))

                // Then: HTTP 401 Unauthorized 응답을 받고, 응답 내용이 기대하는 에러 메시지와 일치해야 함
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid Token"));
    }
}
