package dough.feedback.feedbackFixture;

import dough.feedback.domain.Feedback;
import dough.login.domain.type.SocialLoginType;
import dough.member.domain.Member;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.type.QuestType;

import static dough.burnout.fixture.BurnoutFixture.ENTHUSIAST;

public class FeedbackFixture {

    public static final Member MEMBER1 = new Member(
            1L,
            "kimjunhee",
            "junhee",
            SocialLoginType.KAKAO,
            "manuna@dsjkaf.com",
            "student",
            "male",
            2002,
            "소보루"
    );

    public static final Member MEMBER2 = new Member(
            2L,
            "젤리",
            "jelly",
            SocialLoginType.KAKAO,
            "jelly@dsjkaf.com",
            "student",
            "female",
            2000,
            "소보루"
    );

    public static final Member MEMBER3 = new Member(
            3L,
            "hagoeun",
            "hagoeun",
            SocialLoginType.KAKAO,
            "hagoeun@dsjkaf.com",
            "student",
            "female",
            2002,
            "당근"
    );

    public static final Member MEMBER4 = new Member(
            4L,
            "jamie",
            "jamie",
            SocialLoginType.KAKAO,
            "jamie@dsjkaf.com",
            "student",
            "male",
            1988,
            "소보루"
    );

    public static final Feedback FEEDBACK1 = new Feedback(
            MEMBER1,
            new SelectedQuest(
                    MEMBER1,
                    new Quest(
                            1L,
                            "점심시간, 몸과 마음을 건강하게 유지하며",
                            "15분 운동하기",
                            QuestType.DAILY,
                            3,
                            ENTHUSIAST
                    )
            ),
            "img1.png",
            5
    );

    public static final Feedback FEEDBACK2 = new Feedback(
            MEMBER1,
            new SelectedQuest(
                    MEMBER1,
                    new Quest(
                            2L,
                            "아침에 일어나자마자 해야하는 것",
                            "집 주변 10분 산책하기",
                            QuestType.DAILY,
                            1,
                            ENTHUSIAST
                    )
            ),
            "img2.png",
            3
    );

    public static final Feedback FEEDBACK3 = new Feedback(
            MEMBER1,
            new SelectedQuest(
                    MEMBER1,
                    new Quest(
                            3L,
                            "자기전에",
                            "양치하기",
                            QuestType.DAILY,
                            1,
                            ENTHUSIAST
                    )
            ),
            "img3.png",
            1
    );

    public static final Feedback FEEDBACK4 = new Feedback(
            MEMBER1,
            new SelectedQuest(
                    MEMBER1,
                    new Quest(
                            4L,
                            "저녁시간, 조용히 앉아",
                            "5분간 명상하기",
                            QuestType.DAILY,
                            1,
                            ENTHUSIAST
                    )
            ),
            "img4.png",
            1
    );
}