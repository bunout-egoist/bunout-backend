package dough.quest.dto.response;

import dough.quest.domain.Quest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedQuestResponse {

    private Long id;
    private String description;
    private String activity;
    private String questType;
    private Integer difficulty;

    public static FixedQuestResponse of(final Quest quest) {
        return new FixedQuestResponse(
                quest.getId(),
                quest.getDescription(),
                quest.getActivity(),
                quest.getQuestType().getCode(),
                quest.getDifficulty()
        );
    }
}
