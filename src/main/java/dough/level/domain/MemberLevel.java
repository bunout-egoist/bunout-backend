package dough.level.domain;

import dough.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberLevel {

    private final Member member;
    private final Level level;
    private final Boolean isLevelUp;

    public MemberLevel(
            final Member member,
            final Level level,
            final Boolean isLevelUp
    ) {
        this.member = member;
        this.level = level;
        this.isLevelUp = isLevelUp;
    }
}
