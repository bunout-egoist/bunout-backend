package dough.quest.fixture;

import dough.quest.domain.Quest;

import static dough.burnout.fixture.BurnoutFixture.SOBORO;
import static dough.keyword.fixture.KeywordFixture.INSIDE_ALONE;
import static dough.keyword.fixture.KeywordFixture.OUTSIDE_ALONE;
import static dough.quest.domain.type.QuestType.BY_TYPE;
import static dough.quest.domain.type.QuestType.FIXED;

public class QuestFixture {

    public static final Quest BY_TYPE_QUEST1 = new Quest(
            1L,
            "15분 운동하기",
            "점심시간, 몸과 마음을 건강하게 유지하며",
            BY_TYPE,
            5,
            SOBORO,
            OUTSIDE_ALONE
    );

    public static final Quest BY_TYPE_QUEST2 = new Quest(
            2L,
            "5분간 새로운 책 읽기",
            "아침시간, 표지가 예쁜",
            BY_TYPE,
            2,
            SOBORO,
            INSIDE_ALONE
    );

    public static final Quest FIXED_QUEST1 = new Quest(
            3L,
            "5분간 명상하기",
            "아침시간, 조용한 방 안에서",
            FIXED,
            2,
            SOBORO,
            INSIDE_ALONE
    );

    public static final Quest FIXED_QUEST2 = new Quest(
            3L,
            "1분간 칭찬하기",
            "아침시간, 거울을 보며",
            FIXED,
            3,
            SOBORO,
            INSIDE_ALONE
    );
}
