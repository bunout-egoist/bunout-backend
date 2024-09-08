package dough.login.dto.response;

import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
    private final Boolean isNewMember;

    public static LoginResponse of(
            final String accessToken,
            final Member member,
            final Boolean isNewMember
    ) {
        return new LoginResponse(
                accessToken,
                member.getRefreshToken(),
                isNewMember
        );
    }
}
