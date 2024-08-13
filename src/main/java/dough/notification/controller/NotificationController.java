package dough.notification.controller;

import dough.notification.dto.response.NotificationResponse;
import dough.notification.service.NotificationService;
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
    public ResponseEntity<Void> updateAllNotifications(@PathVariable("memberId") final Long memberId) {
        notificationService.updateAllNotifications(memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> updateNotification(@PathVariable("notificationId") final Long notificationId) {
        notificationService.update(notificationId);
        return ResponseEntity.noContent().build();
    }
}
