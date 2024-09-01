package dough.quest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompletedQuestsTotalResponse {

    private final Long dailyTotal;
    private final Long specialTotal;

    public static CompletedQuestsTotalResponse of(final Long dailyTotal, final Long specialTotal) {
        return new CompletedQuestsTotalResponse(
                dailyTotal,
                specialTotal
        );
    }
}
