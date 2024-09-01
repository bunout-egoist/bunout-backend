package dough.keyword.domain.type;

import dough.keyword.domain.Keyword;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum ParticipationType {

    TOGETHER("함께"),
    ALONE("혼자서"),
    ANYONE("누구와도");

    private final String code;

    ParticipationType(final String code) {
        this.code = code;
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
}
