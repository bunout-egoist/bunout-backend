package dough.level.domain;

import dough.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberLevel {

    private final Member member;
    private final Integer previousLevel;
    private final Boolean isLevelUp;

    public MemberLevel(
            final Member member,
            final Integer previousLevel,
            final Boolean isLevelUp
    ) {
        this.member = member;
        this.previousLevel = previousLevel;
        this.isLevelUp = isLevelUp;
    }
}
