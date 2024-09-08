package dough.login.domain.type;

import lombok.Getter;

@Getter
public enum SocialLoginType {

    KAKAO("kakao"),
    APPLE("apple");

    private final String code;

    SocialLoginType(final String code) {
        this.code = code;
    }
}
