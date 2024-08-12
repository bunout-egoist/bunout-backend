package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TodayQuestResponse {

    private final String activity;
    private final String description;

    public static TodayQuestResponse of(final Quest quest) {
        return new TodayQuestResponse(
                quest.getActivity(),
                quest.getDescription()
        );
    }
}
