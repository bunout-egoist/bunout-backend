package dough.signout.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignoutResponseDTO {
    private final Long memberId;

    public static SignoutResponseDTO of(Long memberId) {
        return new SignoutResponseDTO(memberId);
    }
}
