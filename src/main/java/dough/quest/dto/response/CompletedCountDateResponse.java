package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CompletedCountDateResponse {

    private final LocalDate completedAt;
    private final Long completedQuestCount;

    public static CompletedCountDateResponse of(final LocalDate completedAt, final Long completedQuestCount) {
        return new CompletedCountDateResponse(completedAt, completedQuestCount);
    }
}
