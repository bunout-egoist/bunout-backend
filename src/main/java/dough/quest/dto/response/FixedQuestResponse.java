package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FixedQuestResponse {

    private final Long questId;
    private final String activity;
    private final String description;

    public static FixedQuestResponse of(final Quest quest) {
        return new FixedQuestResponse(
                quest.getId(),
                quest.getActivity(),
                quest.getDescription()
        );
    }
}
