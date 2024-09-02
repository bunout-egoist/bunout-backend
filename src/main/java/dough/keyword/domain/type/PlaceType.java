package dough.keyword.domain.type;

import dough.global.exception.InvalidDomainException;
import dough.keyword.domain.Keyword;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.INVALID_PLACE_TYPE;

@Getter
public enum PlaceType {

    OUTSIDE("밖에서", true),
    INSIDE("안에서", false),
    ANYWHERE("어디서든", null);

    private final String code;
    private final Boolean isOutside;

    PlaceType(
            final String code,
            final Boolean isOutside) {
        this.code = code;
        this.isOutside = isOutside;
    }

    public static String getPlaceCode(final List<Keyword> keywords) {
        final Map<Boolean, List<Keyword>> groupedKeyword = keywords.stream()
                .collect(Collectors.groupingBy(Keyword::getIsOutside));

        if (groupedKeyword.size() == 2) {
            return ANYWHERE.getCode();
        }

        return groupedKeyword.containsKey(true)
                ? OUTSIDE.getCode()
                : INSIDE.getCode();
    }

    public static PlaceType getMappedPlaceType(final Boolean placeIsOutside) {
        return Arrays.stream(values())
                .filter(value -> value.isOutside.equals(placeIsOutside))
                .findAny()
                .orElseThrow(() -> new InvalidDomainException(INVALID_PLACE_TYPE));
    }
}