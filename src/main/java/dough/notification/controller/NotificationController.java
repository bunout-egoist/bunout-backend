package dough.notification.controller;

import dough.notification.dto.request.NotificationsUpdateRequest;
import dough.notification.dto.response.NotificationResponse;
import dough.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{memberId}")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@PathVariable("memberId") final Long memberId) {
        final List<NotificationResponse> notificationResponses = notificationService.getAllNotifications(memberId);
        return ResponseEntity.ok().body(notificationResponses);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<List<NotificationResponse>> updateNotifications(
            @PathVariable("memberId") final Long memberId,
            @RequestBody @Valid final NotificationsUpdateRequest notificationsUpdateRequest) {
        final List<NotificationResponse> notificationResponses = notificationService.updateNotifications(memberId, notificationsUpdateRequest);
        return ResponseEntity.ok().body(notificationResponses);
    }
}
