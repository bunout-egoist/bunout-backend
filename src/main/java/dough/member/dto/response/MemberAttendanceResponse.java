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
        final Level currentLevel = memberLevel.getLevels().get(0);

        if (memberLevel.getLevels().size() > 1) {
            final Level nextLevel = memberLevel.getLevels().get(1);
            return createResponseWithNextLevel(memberLevel, currentLevel, nextLevel);
        } else {
            return createResponseWithoutNextLevel(memberLevel, currentLevel);
        }
    }

    private static MemberAttendanceResponse createResponseWithNextLevel(
            final MemberLevel memberLevel,
            final Level currentLevel,
            final Level nextLevel) {

        return new MemberAttendanceResponse(
                currentLevel.getLevel(),
                nextLevel.getLevel(),
                nextLevel.getRequiredExp() - memberLevel.getMember().getExp(),
                nextLevel.getRequiredExp() - currentLevel.getRequiredExp(),
                memberLevel.getIsLevelUp(),
                memberLevel.getMember().getAttendanceCount()
        );
    }

    private static MemberAttendanceResponse createResponseWithoutNextLevel(
            final MemberLevel memberLevel,
            final Level currentLevel) {

        return new MemberAttendanceResponse(
                currentLevel.getLevel(),
                null,
                null,
                null,
                memberLevel.getIsLevelUp(),
                memberLevel.getMember().getAttendanceCount()
        );
    }
}
