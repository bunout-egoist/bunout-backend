package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CompletedQuestCountResponse {

    private final LocalDate date;
    private final Long completedQuestCount;

    public static CompletedQuestCountResponse of(final LocalDate date, final Long completedQuestCount) {
        return new CompletedQuestCountResponse(date, completedQuestCount);
    }
}
