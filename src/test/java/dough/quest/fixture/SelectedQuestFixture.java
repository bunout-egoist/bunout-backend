package dough.quest.fixture;

import dough.quest.domain.SelectedQuest;

import static dough.feedback.fixture.FeedbackFixture.*;
import static dough.member.fixture.MemberFixture.MEMBER1;
import static dough.member.fixture.MemberFixture.MEMBER2;
import static dough.quest.domain.type.QuestStatus.*;
import static dough.quest.fixture.QuestFixture.*;

public class SelectedQuestFixture {

    public static final SelectedQuest COMPLETED_QUEST1 = new SelectedQuest(
            1L,
            MEMBER1,
            DAILY_QUEST1,
            FEEDBACK1,
            COMPLETED
    );

    public static final SelectedQuest COMPLETED_QUEST2 = new SelectedQuest(
            2L,
            MEMBER1,
            DAILY_QUEST2,
            FEEDBACK2,
            COMPLETED
    );
}
