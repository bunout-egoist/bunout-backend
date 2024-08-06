package dough.quest.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalCompletedQuestsElement {

    private Long dailyAndFixedTotal;
    private Long specialTotal;
}
