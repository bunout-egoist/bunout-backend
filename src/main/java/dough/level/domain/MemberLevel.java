package dough.level.domain;

import dough.member.domain.Member;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberLevel {

    private final Member member;
    private final List<Level> levels;
    private final Boolean isLevelUp;

    public MemberLevel(
            final Member member,
            final List<Level> levels,
            final Boolean isLevelUp
    ) {
        this.member = member;
        this.levels = levels;
        this.isLevelUp = isLevelUp;
    }
}
