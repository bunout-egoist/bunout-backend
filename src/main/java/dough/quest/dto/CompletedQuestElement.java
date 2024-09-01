package dough.quest.dto;

import dough.quest.domain.Quest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedQuestElement {

    private Quest quest;
    private String imageUrl;
    private LocalDate completedDate;
}