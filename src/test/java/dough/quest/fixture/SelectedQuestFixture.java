package dough.quest.fixture;

import dough.quest.domain.SelectedQuest;

import static dough.feedback.fixture.FeedbackFixture.FEEDBACK1;
import static dough.feedback.fixture.FeedbackFixture.FEEDBACK2;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.QuestFixture.BY_TYPE_QUEST1;
import static dough.quest.fixture.QuestFixture.BY_TYPE_QUEST2;

public class SelectedQuestFixture {

    public static final SelectedQuest COMPLETED_QUEST1 = new SelectedQuest(
            1L,
            GOEUN,
            BY_TYPE_QUEST1,
            FEEDBACK1
    );

    public static final SelectedQuest COMPLETED_QUEST2 = new SelectedQuest(
            2L,
            GOEUN,
            BY_TYPE_QUEST2,
            FEEDBACK2
    );

    public static final SelectedQuest IN_PROGRESS_QUEST1 = new SelectedQuest(
            GOEUN,
            BY_TYPE_QUEST1
    );


    public static final SelectedQuest IN_PROGRESS_QUEST2 = new SelectedQuest(
            GOEUN,
            BY_TYPE_QUEST2
    );
}
