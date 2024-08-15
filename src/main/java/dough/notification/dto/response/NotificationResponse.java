package dough.notification.dto.response;

import dough.notification.domain.Notification;
import dough.notification.domain.type.NotificationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationResponse {

    private final NotificationType notificationType;
    private final Boolean isChecked;

    public static NotificationResponse of(final Notification notification) {
        return new NotificationResponse(notification.getNotificationType(), notification.getIsChecked());
    }
}
