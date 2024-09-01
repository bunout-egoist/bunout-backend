package dough.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationsUpdateRequest {

    @NotNull(message = "수정할 알림을 입력해주세요.")
    private List<NotificationUpdateRequest> notifications;
}
