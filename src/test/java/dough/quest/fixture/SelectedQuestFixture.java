package dough.quest.fixture;

import dough.quest.domain.SelectedQuest;

import static dough.feedback.fixture.FeedbackFixture.*;
import static dough.member.fixture.MemberFixture.MEMBER1;
import static dough.member.fixture.MemberFixture.MEMBER2;
import static dough.quest.domain.type.QuestStatus.COMPLETED;
import static dough.quest.domain.type.QuestStatus.PASS;
import static dough.quest.fixture.QuestFixture.*;

public class SelectedQuestFixture {

    public static final SelectedQuest COMPLETED_QUEST1 = new SelectedQuest(
            MEMBER1,
            DAILY_QUEST1,
            FEEDBACK1,
            COMPLETED
    );

    public static final SelectedQuest COMPLETED_QUEST2 = new SelectedQuest(
            MEMBER1,
            DAILY_QUEST2,
            FEEDBACK2,
            COMPLETED
    );

    public static final SelectedQuest COMPLETED_QUEST3 = new SelectedQuest(
            MEMBER2,
            SPECIAL_QUEST1,
            FEEDBACK3,
            COMPLETED
    );

    public static final SelectedQuest IN_PROGRESS_QUEST1 = new SelectedQuest(
            MEMBER2,
            DAILY_QUEST3
    );

    public static final SelectedQuest PASS_QUEST1 = new SelectedQuest(
            MEMBER1,
            SPECIAL_QUEST1,
            PASS
    );
}
