package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CompletedQuestCountDetailResponse {

    private final LocalDate completedDate;
    private final Long dailyAndFixedCount;

    public static CompletedQuestCountDetailResponse of(final LocalDate completedDate, final Long dailyAndFixedCount) {
        return new CompletedQuestCountDetailResponse(completedDate, dailyAndFixedCount);
    }
}
