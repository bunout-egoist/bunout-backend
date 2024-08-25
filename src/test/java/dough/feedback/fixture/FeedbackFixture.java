package dough.feedback.fixture;

import dough.feedback.domain.Feedback;

import static dough.member.fixture.MemberFixture.*;
import static dough.quest.fixture.SelectedQuestFixture.*;

public class FeedbackFixture {

    public static final Feedback FEEDBACK1 = new Feedback(
            1L,
            GOEUN,
            COMPLETED_QUEST1,
            "https://~",
            5
    );

    public static final Feedback FEEDBACK2 = new Feedback(
            2L,
            GOEUN,
            COMPLETED_QUEST2,
            "https://~",
            2
    );
}
