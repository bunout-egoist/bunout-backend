package dough.pushNotification.controller;

import dough.pushNotification.service.PushNotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/send-daily-quest")
    public void sendDailyQuest() {
        pushNotificationService.sendDailyQuest();
    }

    @PostMapping("/send-left-quest")
    public void sendLeftQuest() {
        pushNotificationService.sendLeftQuest();
    }

    @PostMapping("/send-special-quest")
    public void sendSpecialQuest() {
        pushNotificationService.sendSpecialQuest();
    }
}
