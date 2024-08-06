package dough.quest.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedCountDateElement {

    private LocalDate completedAt;
    private Long dailyAndFixedCount;
    private Long specialCount;
}
