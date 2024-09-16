package dough.member.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.level.domain.MemberLevel;
import dough.level.service.LevelService;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberAttendanceResponse;
import dough.member.dto.response.MemberInfoResponse;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
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
    private final SelectedQuestRepository selectedQuestRepository;
    private final LevelService levelService;
    private final QuestService questService;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(final Long memberId) {
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MemberInfoResponse.of(member);
    }

    public MemberInfoResponse updateMemberInfo(final Long memberId, final MemberInfoRequest memberInfoRequest) {
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        member.updateMember(memberInfoRequest.getNickname());
        memberRepository.save(member);

        return MemberInfoResponse.of(member);
    }

    public void updateBurnout(final Long memberId, final BurnoutRequest burnoutRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Burnout burnout = burnoutRepository.findById(burnoutRequest.getBurnoutId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));

        final Quest fixedQuest = questRepository.findById(burnoutRequest.getFixedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));

        final LocalDate currentDate = LocalDate.now();
        validateBurnoutUpdate(member.getBurnoutLastModified(), currentDate);

        updateTodayByTypeQuests(member, burnout, currentDate);
        updateTodayFixedQuest(member, fixedQuest);

        member.updateBurnout(burnout, currentDate);
        member.updateFixedQuest(fixedQuest, member.getFixedQuestLastModified());

        memberRepository.save(member);
    }

    public void updateFixedQuest(final Long memberId, final FixedQuestRequest fixedQuestRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final LocalDate currentDate = LocalDate.now();

        validFixedQuestUpdate(member.getFixedQuestLastModified(), currentDate);

        final Quest fixedQuest = questRepository.findById(fixedQuestRequest.getFixedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));

        updateTodayFixedQuest(member, fixedQuest);

        member.updateFixedQuest(fixedQuest, currentDate);
        memberRepository.save(member);
    }

    public MemberAttendanceResponse checkAttendance(final Long memberId) {
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final LocalDateTime currentAt = LocalDateTime.now();
        final LocalDate startOfWeek = currentAt.toLocalDate().with(TemporalAdjusters.previousOrSame(MONDAY));
        final LocalDate lastAttendanceDate = member.getAttendanceAt().toLocalDate();

        if (lastAttendanceDate.equals(currentAt.toLocalDate())) {
            final MemberLevel memberLevel = levelService.updateLevel(member);
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

    private void updateTodayByTypeQuests(final Member member, final Burnout burnout, final LocalDate currentDate) {
        if (!member.getBurnout().equals(burnout)) {
            final List<SelectedQuest> todayByTypeQuests = questService.updateTodayByTypeQuests(member, burnout, currentDate);
            if (!todayByTypeQuests.isEmpty()) {
                selectedQuestRepository.saveAll(todayByTypeQuests);
            }
        }
    }

    private void updateTodayFixedQuest(final Member member, final Quest fixedQuest) {
        if (!member.getQuest().equals(fixedQuest)) {
            final SelectedQuest selectedQuest = new SelectedQuest(member, fixedQuest);
            selectedQuestRepository.save(selectedQuest);
        }
    }

    private void validateBurnoutUpdate(final LocalDate lastModified, final LocalDate currentDate) {
        if (Optional.ofNullable(lastModified).isPresent()) {

            if (currentDate.withDayOfMonth(1).equals(lastModified.withDayOfMonth(1))) {
                throw new BadRequestException(ALREADY_UPDATED_BURNOUT_TYPE);
            }
        }
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
