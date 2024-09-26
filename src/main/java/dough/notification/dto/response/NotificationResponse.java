package dough.notification.dto.response;

import dough.notification.domain.Notification;
import dough.notification.domain.type.NotificationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationResponse {

    private final Long id;
    private final NotificationType notificationType;
    private final Boolean isChecked;
    private final Boolean isFcmExisted;

    public static NotificationResponse of(final Notification notification) {
        boolean flag_fcmToken = notification.getMember().getNotificationToken() == null;
        boolean flag_isChecked = true;
        if(!flag_fcmToken) {flag_isChecked=false;}
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationType(),
                flag_isChecked,
                flag_fcmToken
        );
    }
}
