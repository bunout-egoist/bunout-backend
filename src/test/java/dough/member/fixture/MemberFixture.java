package dough.member.fixture;

import dough.member.domain.Member;

import static dough.login.domain.type.SocialLoginType.APPLE;
import static dough.login.domain.type.SocialLoginType.KAKAO;

public class MemberFixture {

    public static final Member MEMBER1 = new Member(
            1L,
            "goeun",
            "0000",
            KAKAO,
            "goeun@mail.com",
            "기타",
            "여성",
            2002,
            "빵"
    );

    public static final Member MEMBER2 = new Member(
            2L,
            "heeji",
            "1111",
            APPLE,
            "heeji@mail.com",
            "기타",
            "여성",
            1999,
            "빵"
    );

    public static final Member MEMBER3 = new Member(
            2L,
            "hanni",
            "2222",
            APPLE,
            "hanni@mail.com",
            "기타",
            "여성",
            2004,
            "빵"
    );
}
