package dough.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.feedback.service.FeedbackService;
import dough.global.AbstractControllerTest;
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
import static dough.level.fixture.LevelFixture.LEVEL2;
import static dough.member.fixture.MemberFixture.GOEUN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class FeedbackControllerTest extends AbstractControllerTest {

    private static final String MEMBER_TOKENS = "Bearer accessToken";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        when(tokenProvider.validToken(any()))
                .thenReturn(true);
        given(tokenProvider.getMemberIdFromToken(any()))
                .willReturn(1L);
    }

    @DisplayName("피드백을 생성할 수 있다.")
    @Test
    void createFeedback() throws Exception {
        // given
        final FeedbackRequest feedbackRequest = new FeedbackRequest(
                "png1",
                1L,
                5
        );

        GOEUN.updateExp(40);
        GOEUN.updateLevel(LEVEL2);

        final FeedbackResponse feedbackResponse = new FeedbackResponse(5, true);

        when(feedbackService.createFeedback(any(FeedbackRequest.class)))
                .thenReturn(feedbackResponse);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/feedbacks")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackRequest))
                .header(AUTHORIZATION, MEMBER_TOKENS));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("엑세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("selectedQuestId")
                                        .type(NUMBER)
                                        .description("선택된 퀘스트 아이디")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("imageUrl")
                                        .type(STRING)
                                        .description("이미지 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty")
                                        .type(NUMBER)
                                        .description("난이도")
                                        .attributes(field("constraint", "1-5 사이의 정수"))
                        ),
                        responseFields(
                                fieldWithPath("currentLevel")
                                        .type(NUMBER)
                                        .description("현재 레벨")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("isLevelUp")
                                        .type(BOOLEAN)
                                        .description("레벨업 유무")
                                        .attributes(field("constraint", "불리언"))
                        )
                ));
    }
}