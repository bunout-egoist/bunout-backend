package dough.quest.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class QuestFeedback {

    private final Quest quest;
    private final String imageUrl;

    public QuestFeedback(
            final Quest quest,
            final String imageUrl
    ) {
        this.quest = quest;
        this.imageUrl = imageUrl;
    }
}
