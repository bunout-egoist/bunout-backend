package dough.member.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.level.domain.MemberLevel;
import dough.level.service.LevelService;
import dough.login.service.TokenService;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberAttendanceResponse;
import dough.member.dto.response.MemberInfoResponse;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static dough.global.exception.ExceptionCode.*;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final QuestRepository questRepository;
    private final BurnoutRepository burnoutRepository;
    private final LevelService levelService;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo() {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MemberInfoResponse.of(member);
    }

    public MemberInfoResponse updateMemberInfo(final MemberInfoRequest memberInfoRequest) {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        member.updateMember(memberInfoRequest.getNickname());
        memberRepository.save(member);

        return MemberInfoResponse.of(member);
    }

    public void updateBurnout(final BurnoutRequest burnoutRequest) {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Burnout burnout = burnoutRepository.findById(burnoutRequest.getBurnoutId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));

        final LocalDate currentDate = LocalDate.now();
        validateBurnoutUpdate(member.getBurnoutLastModified(), currentDate);

        member.updateBurnout(burnout, currentDate);
        memberRepository.save(member);
    }

    private void validateBurnoutUpdate(final LocalDate lastModified, final LocalDate currentDate) {
        if (Optional.ofNullable(lastModified).isPresent()) {

            if (currentDate.withDayOfMonth(1).equals(lastModified.withDayOfMonth(1))) {
                throw new BadRequestException(ALREADY_UPDATED_BURNOUT_TYPE);
            }
        }
    }

    public void updateFixedQuest(final FixedQuestRequest fixedQuestRequest) {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Quest quest = questRepository.findById(fixedQuestRequest.getFixedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));

        final LocalDate currentDate = LocalDate.now();
        validFixedQuestUpdate(member.getFixedQuestLastModified(), currentDate);

        member.updateFixedQuest(quest, currentDate);
        memberRepository.save(member);
    }

    public MemberAttendanceResponse checkAttendance() {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final LocalDateTime currentAt = LocalDateTime.now();
        final LocalDate startOfWeek = currentAt.toLocalDate().with(TemporalAdjusters.previousOrSame(MONDAY));
        final LocalDate lastAttendanceDate = member.getAttendanceAt().toLocalDate();

        if (lastAttendanceDate.equals(currentAt.toLocalDate())) {
            final MemberLevel memberLevel = new MemberLevel(member, member.getLevel(), false);
            return MemberAttendanceResponse.of(memberLevel);
        }

        final Integer attendanceCount = member.getAttendanceCount();
        final Integer expAfterAttendance = member.getExp() + 5;

        if (lastAttendanceDate.isBefore(startOfWeek)) {
            member.updateAttendance(currentAt, 1, expAfterAttendance);
        } else {
            member.updateAttendance(currentAt, attendanceCount + 1, expAfterAttendance);
        }

        final MemberLevel memberLevel = levelService.updateLevel(member);
        memberRepository.save(memberLevel.getMember());

        return MemberAttendanceResponse.of(memberLevel);
    }

    private void validFixedQuestUpdate(final LocalDate lastModified, final LocalDate currentDate) {
        if (Optional.ofNullable(lastModified).isPresent()) {

            final LocalDate startOfWeek = currentDate.with(MONDAY);
            final LocalDate endOfWeek = currentDate.with(SUNDAY);

            if (!lastModified.isBefore(startOfWeek) && !lastModified.isAfter(endOfWeek)) {
                throw new BadRequestException(ALREADY_UPDATED_FIXED_QUEST);
            }
        }
    }
}
