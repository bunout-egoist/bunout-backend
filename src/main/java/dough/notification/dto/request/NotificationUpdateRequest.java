package dough.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationUpdateRequest {

    @NotBlank(message = "알림 아이디를 입력해주세요.")
    private Long id;

    @NotBlank(message = "알림 유무를 입력해주세요.")
    private Boolean isChecked;
}