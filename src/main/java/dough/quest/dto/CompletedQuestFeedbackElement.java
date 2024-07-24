package dough.quest.dto;

import dough.feedback.domain.Feedback;
import dough.quest.domain.Quest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompletedQuestFeedbackElement {
    private Quest quest;
    private Feedback feedback;
}
