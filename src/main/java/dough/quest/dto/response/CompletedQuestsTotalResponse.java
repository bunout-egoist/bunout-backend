package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompletedQuestsTotalResponse {

    private final Long dailyCount;
    private final Long specialCount;

    public static CompletedQuestsTotalResponse of(final Long dailyCount, final Long specialCount) {
        return new CompletedQuestsTotalResponse(
                dailyCount,
                specialCount
        );
    }
}
