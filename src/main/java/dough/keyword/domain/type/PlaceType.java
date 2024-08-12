package dough.keyword.domain.type;

import dough.keyword.domain.Keyword;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum PlaceType {

    OUTSIDE("밖에서"),
    INSIDE("안에서"),
    ANYWHERE("어디서든");

    private final String code;

    PlaceType(final String code) {
        this.code = code;
    }

    public static String getPlaceCode(final List<Keyword> keyword) {
        keyword.forEach();
    }
}
