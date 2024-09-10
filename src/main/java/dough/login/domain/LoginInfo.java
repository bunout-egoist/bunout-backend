package dough.login.domain;

import dough.login.domain.type.SocialLoginType;
import lombok.Getter;

@Getter
public class LoginInfo {

    private final String socialLoginId;
    private final SocialLoginType socialLoginType;

    public LoginInfo(
            final String socialLoginId,
            final SocialLoginType socialLoginType
    ) {
        this.socialLoginId = socialLoginId;
        this.socialLoginType = socialLoginType;
    }
}
