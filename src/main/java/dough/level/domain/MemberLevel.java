package dough.level.domain;

import dough.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberLevel {

    private final Member member;
    private final Level level;
    private final Boolean isLevelUp;
    private final Integer requiredExp;

    public MemberLevel(
            final Member member,
            final Level level,
            final Boolean isLevelUp,
            final Integer requiredExp
    ) {
        this.member = member;
        this.level = level;
        this.isLevelUp = isLevelUp;
        this.requiredExp = requiredExp;
    }
}
