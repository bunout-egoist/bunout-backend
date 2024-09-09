package dough.member.dto.response;

import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberAttendanceResponse {

    private final String nickname;
    private final Integer currentLevel;
    private final Integer nextLevel;
    private final Integer currentExp;
    private final Integer requiredExp;
    private final Boolean isLevelUp;
    private final Integer attendanceCount;

    public static MemberAttendanceResponse of(final MemberLevel memberLevel) {
        final Level currentLevel = memberLevel.getLevel();
        final Integer requiredExp = currentLevel.getRequiredExp();
        final Member member = memberLevel.getMember();

        return new MemberAttendanceResponse(
                member.getNickname(),
                currentLevel.getLevel(),
                currentLevel.getLevel() + 1,
                requiredExp - (currentLevel.getAccumulatedExp() - member.getExp()),
                requiredExp,
                memberLevel.getIsLevelUp(),
                member.getAttendanceCount()
        );
    }
}
