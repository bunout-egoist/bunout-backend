package dough.pushNotification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FcmTokenRequest {
    private String fcmToken;
}