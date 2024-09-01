package dough.quest.dto;

import dough.quest.domain.Quest;
import dough.quest.domain.QuestFeedback;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CompletedQuestElements {

    private final List<CompletedQuestElement> completedQuestElements;

    public Map<LocalDate, List<QuestFeedback>> toQuestFeedbackMap() {
        Map<LocalDate, List<QuestFeedback>> questFeedbackMap = new HashMap<>();
        for (final CompletedQuestElement completedQuestElement : completedQuestElements) {
            final LocalDate completedDate = completedQuestElement.getCompletedDate();
            final Quest quest = completedQuestElement.getQuest();
            final String imageUrl = completedQuestElement.getImageUrl();
            questFeedbackMap.computeIfAbsent(completedDate, k -> new ArrayList<>()).add(new QuestFeedback(quest, imageUrl));
        }
        return questFeedbackMap;
    }
}
