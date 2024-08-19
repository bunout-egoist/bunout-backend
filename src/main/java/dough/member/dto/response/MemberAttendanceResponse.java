package dough.member.dto.response;

import dough.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberAttendanceResponse {

    private final Integer level;
    private final Integer attendanceCount;

    public static MemberAttendanceResponse of(final Member member) {
        return new MemberAttendanceResponse(member.getLevel().getLevel(), member.getAttendanceCount());
    }
}
