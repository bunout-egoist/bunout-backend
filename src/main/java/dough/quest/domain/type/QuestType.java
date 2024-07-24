package dough.quest.domain.type;

import lombok.Getter;

@Getter
public enum QuestType {

    DAILY("데일리퀘스트"),
    SPECIAL("스페셜퀘스트");

    private final String code;

    QuestType(final String code) {
        this.code = code;
    }
}
