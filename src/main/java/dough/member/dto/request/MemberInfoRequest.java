package dough.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 5, message = "닉네임은 5자를 초과할 수 없습니다.")
    private String nickname;
}
