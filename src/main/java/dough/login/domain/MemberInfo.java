package dough.login.domain;

import dough.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberInfo {

    private final Member member;
    private final Boolean isNewMember;

    public MemberInfo(
            final Member member,
            final Boolean isNewMember
    ) {
        this.member = member;
        this.isNewMember = isNewMember;
    }
}
