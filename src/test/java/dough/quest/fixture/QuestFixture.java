package dough.quest.fixture;

import dough.quest.domain.Quest;

import static dough.quest.domain.type.QuestType.DAILY;
import static dough.quest.domain.type.QuestType.SPECIAL;

public class QuestFixture {

    public static final Quest DAILY_QUEST1 = new Quest(
            1L,
            "점심시간, 몸과 마음을 건강하게 유지하며",
            "15분 운동하기",
            DAILY,
            3
    );

    public static final Quest DAILY_QUEST2 = new Quest(
            2L,
            "아침시간, 표지가 예쁜",
            "5분간 새로운 책 읽기",
            DAILY,
            2
    );
}