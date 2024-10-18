package dough.pushNotification;

import dough.login.domain.Accessor;
import dough.login.domain.Auth;
import dough.notification.dto.response.NotificationResponse;
import dough.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm/test")
public class ControllerFCM {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/notice")
    public void sendFCM() {
        pushNotificationService.sendDailyQuest();
    }
}
