package dough.keyword;

import lombok.Getter;

@Getter
public class KeywordCode {
    private final String placeCode;
    private final String participationCode;

    public KeywordCode(
            final String placeCode,
            final String participationCode
    ) {
        this.placeCode = placeCode;
        this.participationCode = participationCode;
    }
}
