package dough.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.feedback.service.FeedbackService;
import dough.global.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class FeedbackControllerTest extends AbstractControllerTest {

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
        FeedbackRequest feedbackRequest = new FeedbackRequest(1L, 5);
        MockMultipartFile feedbackRequestFile = new MockMultipartFile(
                "feedback",
                "",
                "application/json",
                new ObjectMapper().writeValueAsBytes(feedbackRequest)
        );

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        // Mock 서비스 응답 설정
        FeedbackResponse feedbackResponse = new FeedbackResponse(5, true, "https://asdkfl.com");
        given(feedbackService.createFeedback(any(FeedbackRequest.class), any(MultipartFile.class)))
                .willReturn(feedbackResponse);

        // Multipart 요청 전송
        ResultActions resultActions = mockMvc.perform(multipart("/api/v1/feedbacks")
                .file(feedbackRequestFile)  // 'feedback' 파트
                .file(multipartFile)  // 'file' 파트
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // 응답 검증
        resultActions.andExpect(status().isOk())
                .andDo(document(
                        "create-feedback",
                        requestParts(
                                partWithName("feedback").description("피드백 요청 객체"),
                                partWithName("file").description("파일")
                        ),
                        responseFields(
                                fieldWithPath("currentLevel").type(NUMBER).description("현재 레벨"),
                                fieldWithPath("isLevelUp").type(BOOLEAN).description("레벨업 유무"),
                                fieldWithPath("imageUrl").type(STRING).description("이미지 경로")
                        )
                ));
    }

}