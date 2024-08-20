package dough.member.dto.response;

import dough.level.domain.MemberLevel;
import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberAttendanceResponse {

    private final Integer exp;
    private final Integer previousLevel;
    private final Integer currentLevel;
    private final Boolean isLevelUp;
    private final Integer attendanceCount;

    public static MemberAttendanceResponse of(final MemberLevel memberLevel) {
        final Member member = memberLevel.getMember();

        return new MemberAttendanceResponse(
                member.getExp(),
                memberLevel.getPreviousLevel(),
                member.getLevel().getLevel(),
                memberLevel.getIsLevelUp(),
                member.getAttendanceCount()
        );
    }
}
