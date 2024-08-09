package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TodayQuestResponse {

    private final Boolean isOutside;
    private final Boolean isGroup;
    private final String activity;
    private final String description;

    public static TodayQuestResponse of(final Quest quest) {
        return new TodayQuestResponse(
                false,
                false,
                quest.getActivity(),
                quest.getDescription()
        );
    }
}
