package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TotalCompletedQuestsResponse {

    private final Long dailyAndFixedCount;
    private final Long specialCount;

    public static TotalCompletedQuestsResponse of(final Long dailyAndFixedCount, final Long specialCount) {
        return new TotalCompletedQuestsResponse(
                dailyAndFixedCount,
                specialCount
        );
    }
}
