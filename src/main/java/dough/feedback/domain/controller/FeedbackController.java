package dough.feedback.domain.controller;

import dough.feedback.domain.Feedback;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("feedbacks/{questId}")
    public ResponseEntity<FeedbackResponse> feedback(@PathVariable("questId") Long questId, @Valid @RequestBody FeedbackRequest feedbackRequest) {
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(questId, feedbackRequest);
        return ResponseEntity.ok(feedbackResponse);
    }
}
