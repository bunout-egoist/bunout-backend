package dough.notification.controller;

import dough.auth.Auth;
import dough.login.domain.Accessor;
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
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@Auth final Accessor accessor) {
        final List<NotificationResponse> notificationResponses = notificationService.getAllNotifications(accessor.getMemberId());
        return ResponseEntity.ok().body(notificationResponses);
    }

    @PutMapping
    public ResponseEntity<List<NotificationResponse>> updateNotifications(
            @Auth final Accessor accessor,
            @RequestBody @Valid final NotificationsUpdateRequest notificationsUpdateRequest
    ) {
        final List<NotificationResponse> notificationResponses = notificationService.updateNotifications(accessor.getMemberId(), notificationsUpdateRequest);
        return ResponseEntity.ok().body(notificationResponses);
    }
}
