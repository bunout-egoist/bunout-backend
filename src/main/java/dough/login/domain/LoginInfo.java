package dough.login.domain;

import dough.login.domain.type.SocialLoginType;
import lombok.Getter;

@Getter
public class LoginInfo {

    private final String socialLoginId;
    private final String appleToken;
    private final SocialLoginType socialLoginType;

    public LoginInfo(
            final String socialLoginId,
            final String appleToken,
            final SocialLoginType socialLoginType
    ) {
        this.socialLoginId = socialLoginId;
        this.appleToken = appleToken;
        this.socialLoginType = socialLoginType;
    }

    public LoginInfo(
            final String socialLoginId,
            final SocialLoginType socialLoginType
    ) {
        this(socialLoginId, null, socialLoginType);
    }
}
