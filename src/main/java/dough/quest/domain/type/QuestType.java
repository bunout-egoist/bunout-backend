package dough.quest.domain.type;

import dough.global.exception.InvalidDomainException;
import lombok.Getter;

import java.util.Arrays;

import static dough.global.exception.ExceptionCode.INVALID_QUEST_TYPE;

@Getter
public enum QuestType {

    DAILY("데일리"),
    SPECIAL("스페셜");

    private final String code;

    QuestType(final String code) {
        this.code = code;
    }

    public static QuestType getMappedQuestType(final String questCode) {
        return Arrays.stream(values())
                .filter(value -> value.code.equals(questCode))
                .findAny()
                .orElseThrow(() -> new InvalidDomainException(INVALID_QUEST_TYPE));
    }
}
