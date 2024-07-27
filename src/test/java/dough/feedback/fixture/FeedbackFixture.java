package dough.feedback.fixture;

import dough.feedback.domain.Feedback;

import static dough.member.fixture.MemberFixture.*;
import static dough.quest.fixture.SelectedQuestFixture.*;

public class FeedbackFixture {

    public static final Feedback FEEDBACK1 = new Feedback(
            1L,
            MEMBER1,
            COMPLETED_QUEST1,
            "미션 완료 메시지",
            "https://~",
            5
    );

    public static final Feedback FEEDBACK2 = new Feedback(
            2L,
            MEMBER1,
            COMPLETED_QUEST2,
            "미션 완료 메시지",
            "https://~",
            2
    );
}
