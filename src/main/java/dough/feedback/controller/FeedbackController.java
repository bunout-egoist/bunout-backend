package dough.feedback.controller;

import dough.auth.Auth;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.feedback.service.FeedbackService;
import dough.login.domain.Accessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @Auth final Accessor accessor,
            @Valid @RequestPart(value = "feedback") final FeedbackRequest feedbackRequest, @RequestPart(value = "file") MultipartFile file) {
        final FeedbackResponse feedbackResponse = feedbackService.createFeedback(accessor.getMemberId(), feedbackRequest, file);
        return ResponseEntity.ok().body(feedbackResponse);
    }
}
