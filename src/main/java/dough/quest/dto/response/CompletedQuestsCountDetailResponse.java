package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CompletedQuestsCountDetailResponse {

    private final LocalDate completedDate;
    private final Long dailyCount;

    public static CompletedQuestsCountDetailResponse of(final LocalDate completedDate, final Long dailyCount) {
        return new CompletedQuestsCountDetailResponse(completedDate, dailyCount);
    }
}
