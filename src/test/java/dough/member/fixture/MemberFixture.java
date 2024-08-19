package dough.member.fixture;

import dough.member.domain.Member;

import static dough.burnout.fixture.BurnoutFixture.ENTHUSIAST;
import static dough.level.fixture.LevelFixture.LEVEL1;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.KAKAO;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;

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
            MEMBER,
            LEVEL1,
            FIXED_QUEST1
    );
}
