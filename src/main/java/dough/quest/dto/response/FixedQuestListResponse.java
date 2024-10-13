package dough.quest.dto.response;

import dough.burnout.domain.Burnout;
import dough.member.domain.Member;
import dough.quest.domain.Quest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FixedQuestListResponse {

    private final String nickname;
    private final String burnoutName;
    private final List<FixedQuestResponse> fixedQuests;

    public static final FixedQuestListResponse of(final Member member, final Burnout burnout, final List<Quest> fixedQuests) {
        final List<FixedQuestResponse> fixedQuestResponses = fixedQuests.stream()
                .map(fixedQuest -> FixedQuestResponse.of(fixedQuest))
                .toList();

        return new FixedQuestListResponse(member.getNickname(), burnout.getName(), fixedQuestResponses);
    }
}
