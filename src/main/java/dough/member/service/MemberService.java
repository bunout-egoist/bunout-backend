package dough.member.service;

import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutTypeRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static dough.global.exception.ExceptionCode.ALREADY_UPDATED_BURNOUT_TYPE;
import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

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

    public void changeBurnoutType(final Long memberId, final BurnoutTypeRequest burnoutTypeRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        validateBurnoutTypeUpdate(member.getBurnoutTypeLastModified());

        member.changeBurnoutType(burnoutTypeRequest.getBurnoutType());
        memberRepository.save(member);
    }

    public void validateBurnoutTypeUpdate(final LocalDate lastModified) {
        if (Optional.ofNullable(lastModified).isPresent()) {
            final LocalDate current = LocalDate.now();

            if (current.withDayOfMonth(1).equals(lastModified.withDayOfMonth(1))) {
                throw new BadRequestException(ALREADY_UPDATED_BURNOUT_TYPE);
            }
        }
    }
}
