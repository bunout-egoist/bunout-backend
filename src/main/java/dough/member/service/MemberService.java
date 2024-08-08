package dough.member.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MemberInfoResponse.from(member);
    }

    public MemberInfoResponse updateMemberInfo(final Long memberId, final MemberInfoRequest memberInfoRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        member.updateMember(memberInfoRequest.getNickname());
        memberRepository.save(member);

        return MemberInfoResponse.from(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public void updateBurnout(final Long memberId, final BurnoutRequest burnoutRequest) {
        final Member member = memberRepository.findById(memberId)
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

    public void updateFixedQuest(final Long memberId, final FixedQuestRequest fixedQuestRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Quest quest = questRepository.findById(fixedQuestRequest.getFixedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));

        final LocalDate currentDate = LocalDate.now();
        validFixedQuestUpdate(member.getFixedQuestLastModified(), currentDate);

        member.updateFixedQuest(quest, currentDate);
        memberRepository.save(member);
    }

    private void validFixedQuestUpdate(final LocalDate lastModified, final LocalDate currentDate) {
        if (Optional.ofNullable(lastModified).isPresent()) {

            final LocalDate startOfWeek = currentDate.with(MONDAY);
            final LocalDate endOfWeek = currentDate.with(SUNDAY);

            if (lastModified.isAfter(startOfWeek) && lastModified.isBefore(endOfWeek)) {
                throw new BadRequestException(ALREADY_UPDATED_FIXED_QUEST);
            }
        }
    }
}
