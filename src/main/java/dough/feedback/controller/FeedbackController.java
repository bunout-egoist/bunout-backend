package dough.feedback.controller;

import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestPart(value = "feedback") final FeedbackRequest feedbackRequest, @RequestPart(value = "file") MultipartFile file) {
        final FeedbackResponse feedbackResponse = feedbackService.createFeedback(feedbackRequest, file);
        return ResponseEntity.ok().body(feedbackResponse);
    }
}
