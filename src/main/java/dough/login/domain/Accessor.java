package dough.login.domain;

import dough.login.domain.type.RoleType;
import lombok.Getter;

import static dough.login.domain.type.RoleType.MEMBER;

@Getter
public class Accessor {

    private final Long memberId;
    private final RoleType role;

    public Accessor(
            final Long memberId,
            final RoleType role
    ) {
        this.memberId = memberId;
        this.role = role;
    }

    public static Accessor member(final Long memberId) {
        return new Accessor(memberId, MEMBER);
    }

    public boolean isMember() {
        return MEMBER.equals(role);
    }
}