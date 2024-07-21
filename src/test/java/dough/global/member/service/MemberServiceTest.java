package dough.global.member.service;

import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.login.domain.type.SocialLoginType.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("멤버의 닉네임을 조회할 수 있다.")
    @Test
    void getMemberInfo() {
        // given
        final Member member = new Member(1L, "goeun", "0000", KAKAO, "goeun@mail.com", "기타", "여성", 2002, "빵");
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));

        // when
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(member.getId());

        // then
        assertThat(memberInfoResponse).usingRecursiveComparison().isEqualTo(MemberInfoResponse.from(member));
    }

    @DisplayName("멤버의 닉네임을 수정할 수 있다.")
    @Test
    void updateMemberInfo() {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("jjanggu");
        final Member member = new Member(1L, "goeun", "0000", KAKAO, "goeun@mail.com", "기타", "여성", 2002, "빵");
        member.updateMember("cheolsu");

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(member));
        given(memberRepository.save(any()))
                .willReturn(member);

        // when
        memberService.updateMemberInfo(member.getId(), memberInfoRequest);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());
    }
}
