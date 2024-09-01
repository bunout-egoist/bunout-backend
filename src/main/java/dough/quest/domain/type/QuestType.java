package dough.quest.domain.type;

import dough.global.exception.InvalidDomainException;
import lombok.Getter;

import java.util.Arrays;

import static dough.global.exception.ExceptionCode.INVALID_QUEST_TYPE;

@Getter
public enum QuestType {

    BY_TYPE("유형별퀘스트", 15),
    FIXED("고정퀘스트", 15),
    SPECIAL("스페셜퀘스트", 50);

    private final String code;
    private final Integer exp;

    QuestType(
            final String code,
            final Integer exp
    ) {
        this.code = code;
        this.exp = exp;
    }

    public static QuestType getMappedQuestType(final String questCode) {
        return Arrays.stream(values())
                .filter(value -> value.code.equals(questCode))
                .findAny()
                .orElseThrow(() -> new InvalidDomainException(INVALID_QUEST_TYPE));
    }
}
