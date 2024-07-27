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

    public static final SelectedQuest COMPLETED_QUEST3 = new SelectedQuest(
            3L,
            MEMBER2,
            SPECIAL_QUEST1,
            FEEDBACK3,
            COMPLETED
    );

    public static final SelectedQuest IN_PROGRESS_QUEST1 = new SelectedQuest(
            10L,
            MEMBER2,
            DAILY_QUEST3,
            IN_PROGRESS
    );

    public static final SelectedQuest PASS_QUEST1 = new SelectedQuest(
            20L,
            MEMBER1,
            SPECIAL_QUEST1,
            PASS
    );
}
