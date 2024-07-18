package dough.member.service;

import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoResponse getMemberInfo(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MemberInfoResponse.from(member);
    }
}
