package dough.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BurnoutRequest {

    @NotNull(message = "번아웃 아이디를 입력해주세요.")
    private Long burnoutId;
}
