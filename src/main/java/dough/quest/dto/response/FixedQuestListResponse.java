package dough.quest.dto.response;

import dough.burnout.domain.Burnout;
import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FixedQuestListResponse {

    private final String burnoutName;
    private final List<FixedQuestResponse> fixedQuests;

    public static final FixedQuestListResponse of(final Burnout burnout, final List<Quest> fixedQuests) {
        final List<FixedQuestResponse> fixedQuestResponses = fixedQuests.stream()
                .map(fixedQuest -> FixedQuestResponse.of(fixedQuest))
                .toList();

        return new FixedQuestListResponse(burnout.getName(), fixedQuestResponses);
    }
}
