package dough.feedback.fixture;

import dough.feedback.domain.Feedback;
import dough.quest.domain.Quest;

import java.util.List;

import static dough.feedback.fixture.FeedbackFixture.FEEDBACK1;
import static dough.feedback.fixture.FeedbackFixture.FEEDBACK2;
import static dough.quest.fixture.QuestFixture.BY_TYPE_QUEST1;
import static dough.quest.fixture.QuestFixture.BY_TYPE_QUEST2;

public class CompletedQuestDetailFixture {

    public static final List<CompletedQuestDetail> COMPLETED_QUEST_DETAILS = List.of(
            new CompletedQuestDetail(BY_TYPE_QUEST1, FEEDBACK1),
            new CompletedQuestDetail(BY_TYPE_QUEST2, FEEDBACK2)
    );

    public static class CompletedQuestDetail {

        public Quest quest;
        public Feedback feedback;

        public CompletedQuestDetail(Quest quest, Feedback feedback) {
            this.quest = quest;
            this.feedback = feedback;
        }
    }
}
