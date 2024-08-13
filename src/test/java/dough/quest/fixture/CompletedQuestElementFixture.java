package dough.quest.fixture;

import dough.quest.dto.CompletedQuestElement;

import java.time.LocalDate;

import static dough.quest.fixture.QuestFixture.*;

public class CompletedQuestElementFixture {

    public static final CompletedQuestElement QUEST_ELEMENT1 = new CompletedQuestElement(
            DAILY_QUEST1,
            "https://~",
            LocalDate.of(2024, 8, 11)
    );

    public static final CompletedQuestElement QUEST_ELEMENT2 = new CompletedQuestElement(
            DAILY_QUEST2,
            "https://~",
            LocalDate.of(2024, 8, 11)
    );

    public static final CompletedQuestElement QUEST_ELEMENT3 = new CompletedQuestElement(
            FIXED_QUEST1,
            "https://~",
            LocalDate.of(2024, 8, 14)
    );
}
