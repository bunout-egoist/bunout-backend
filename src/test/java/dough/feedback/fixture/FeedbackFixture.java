package dough.feedback.fixture;

import dough.feedback.domain.Feedback;

import static dough.member.fixture.MemberFixture.MEMBER2;
import static dough.member.fixture.MemberFixture.MEMBER3;
import static dough.quest.fixture.SelectedQuestFixture.COMPLETED_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.COMPLETED_QUEST2;

public class FeedbackFixture {

    public static final Feedback FEEDBACK1 = new Feedback(
            MEMBER2,
            COMPLETED_QUEST1,
            "미션 완료 메시지",
            "https://~",
            5
    );

    public static final Feedback FEEDBACK2 = new Feedback(
            MEMBER2,
            COMPLETED_QUEST2,
            "미션 완료 메시지",
            "https://~",
            2
    );

    public static final Feedback FEEDBACK3 = new Feedback(
            MEMBER3,
            COMPLETED_QUEST1,
            "미션 완료 메시지",
            "https://~",
            3
    );
}
