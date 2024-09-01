package dough.member.dto.response;

import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberAttendanceResponse {

    private final Integer currentLevel;
    private final Integer nextLevel;
    private final Integer currentExp;
    private final Integer requiredExp;
    private final Boolean isLevelUp;
    private final Integer attendanceCount;

    public static MemberAttendanceResponse of(final MemberLevel memberLevel) {
        final Level currentLevel = memberLevel.getLevel();
        return new MemberAttendanceResponse(
                currentLevel.getLevel(),
                currentLevel.getLevel() + 1,
                currentLevel.getAccumulatedExp() - memberLevel.getMember().getExp(),
                currentLevel.getRequiredExp(),
                memberLevel.getIsLevelUp(),
                memberLevel.getMember().getAttendanceCount()
        );
    }
}
