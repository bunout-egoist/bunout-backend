package dough.member.fixture;

import dough.member.domain.Member;

import static dough.burnout.fixture.BurnoutFixture.ENTHUSIAST;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.KAKAO;

public class MemberFixture {

    public static final Member GOEUN = new Member(
            1L,
            "goeun",
            "0000",
            KAKAO,
            "goeun@mail.com",
            "기타",
            "여성",
            2002,
            ENTHUSIAST,
            MEMBER
    );
}
