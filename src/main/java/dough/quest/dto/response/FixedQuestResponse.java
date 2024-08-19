package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class FixedQuestResponse {

    private final Long id;
    private final String description;
    private final String activity;

    public static FixedQuestResponse of(final Quest quest) {
        return new FixedQuestResponse(
                quest.getId(),
                quest.getDescription(),
                quest.getActivity()
        );
    }
}
