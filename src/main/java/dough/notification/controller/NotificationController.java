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

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        final List<NotificationResponse> notificationResponses = notificationService.getAllNotifications();
        return ResponseEntity.ok().body(notificationResponses);
    }

    @PutMapping
    public ResponseEntity<List<NotificationResponse>> updateNotifications(@RequestBody @Valid final NotificationsUpdateRequest notificationsUpdateRequest) {
        final List<NotificationResponse> notificationResponses = notificationService.updateNotifications(notificationsUpdateRequest);
        return ResponseEntity.ok().body(notificationResponses);
    }
}