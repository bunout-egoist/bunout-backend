package dough.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.feedback.domain.controller.FeedbackController;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.feedback.feedbackFixture.FeedbackFixture;
import dough.feedback.service.FeedbackService;
import dough.global.AbstractControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import static dough.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class FeedbackControllerTest extends AbstractControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    @DisplayName("피드백을 생성할 수 있다.")
    @Test
    void createFeedback() throws Exception {
        // given
        Long questId = FeedbackFixture.FEEDBACK1.getSelectedQuest().getQuest().getId();
        FeedbackRequest feedbackRequest = new FeedbackRequest(
                FeedbackFixture.FEEDBACK1.getImageUrl(),
                FeedbackFixture.FEEDBACK1.getDifficulty()
        );
        FeedbackResponse feedbackResponse = FeedbackResponse.from(FeedbackFixture.FEEDBACK1);


        when(feedbackService.createFeedback(any(), any(FeedbackRequest.class)))
                .thenReturn(feedbackResponse);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/feedbacks/{questId}", questId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackRequest)));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("questId").description("퀘스트 아이디")
                        ),
                        requestFields(
                                fieldWithPath("imageUrl").type(STRING).description("이미지 URL").attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty").type(NUMBER).description("난이도").attributes(field("constraint", "1-5 사이의 정수"))
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("피드백 아이디").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("imageUrl").type(STRING).description("이미지 URL").attributes(field("constraint", "문자열")),
                                fieldWithPath("difficulty").type(NUMBER).description("난이도").attributes(field("constraint", "1-5 사이의 정수")),
                                fieldWithPath("memberId").type(NUMBER).description("멤버 아이디").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("selectedQuestId").type(NULL).description("선택된 퀘스트 아이디").attributes(field("constraint", "양의 정수"))
                        )
                ));
    }
}