package dough.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BurnoutTypeRequest {

    @NotBlank(message = "번아웃 타입을 입력해주세요.")
    private String burnoutType;
}
