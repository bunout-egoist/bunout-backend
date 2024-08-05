package dough.dashboard.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TotalCompletedQuestCountResponse {

    private final Long dailyAndFixedCount;
    private final Long specialCount;

    public static TotalCompletedQuestCountResponse of(final Long dailyAndFixedCount, final Long specialCount) {
        return new TotalCompletedQuestCountResponse(
                dailyAndFixedCount,
                specialCount
        );
    }
}
