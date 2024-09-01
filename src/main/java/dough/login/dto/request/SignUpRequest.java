package dough.login.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {
    @NotBlank(message = "access token을 입력해주세요.")
    private String accessToken;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 5, message = "닉네임은 5자 이내로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "성별을 입력해주세요.")
    @Pattern(regexp = "남성|여성|기타", message = "성별은 남성, 여성, 기타 중 하나여야 합니다.")
    private String gender;

    @NotNull(message = "출생연도를 입력해주세요.")
    private int birth_year;

    @NotBlank(message = "직업을 입력해주세요.")
    @Pattern(regexp = "학생|직장인|자영업|주부|무직|기타", message = "직업은 학생, 직장인, 자영업, 주부, 무직, 기타 중 하나여야 합니다.")
    private String occupation;

    @NotNull(message = "고정 퀘스트 아이디를 입력해주세요.")
    private Long fixedQuestId;

    @NotNull(message = "번아웃 아이디를 입력해주세요.")
    private Long bunoutId;

}
