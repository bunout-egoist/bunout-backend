package dough.global.member.fixture;

import dough.login.domain.type.RoleType;
import dough.member.domain.Member;

import static dough.login.domain.type.SocialLoginType.KAKAO;

public class MemberFixture {

    public static final Member MEMBER = new Member(
            1L,
            "goeun",
            "0000",
            KAKAO,
            "goeun@mail.com",
            "기타",
            "여성",
            2002,
            "빵",
            RoleType.MEMBER
    );
}
