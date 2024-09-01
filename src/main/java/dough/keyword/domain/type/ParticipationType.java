package dough.keyword.domain.type;

import dough.global.exception.InvalidDomainException;
import dough.keyword.domain.Keyword;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.INVALID_PARTICIPATION_TYPE;

@Getter
public enum ParticipationType {

    TOGETHER("함께", true),
    ALONE("혼자서", false),
    ANYONE("누구와도", null);

    private final String code;
    private final Boolean isGroup;

    ParticipationType(
            final String code,
            final Boolean isGroup
    ) {
        this.code = code;
        this.isGroup = isGroup;
    }

    public static String getParticipationCode(final List<Keyword> keywords) {
        final Map<Boolean, List<Keyword>> groupedKeyword = keywords.stream()
                .collect(Collectors.groupingBy(Keyword::getIsGroup));

        if (groupedKeyword.size() == 2) {
            return ANYONE.getCode();
        }

        return groupedKeyword.containsKey(true)
                ? TOGETHER.getCode()
                : ALONE.getCode();
    }

    public static ParticipationType getMappedParticipationType(final Boolean participationIsGroup) {
        return Arrays.stream(values())
                .filter(value -> value.isGroup.equals(participationIsGroup))
                .findAny()
                .orElseThrow(() -> new InvalidDomainException(INVALID_PARTICIPATION_TYPE));
    }
}
