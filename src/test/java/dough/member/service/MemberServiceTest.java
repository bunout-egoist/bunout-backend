package dough.member.service;

import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.member.fixture.MemberFixture.MEMBER;
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
        given(memberRepository.findById(MEMBER.getId()))
                .willReturn(Optional.of(MEMBER));

        // when
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(MEMBER.getId());

        // then
        assertThat(memberInfoResponse).usingRecursiveComparison().isEqualTo(MemberInfoResponse.from(MEMBER));
    }

    @DisplayName("멤버의 닉네임을 수정할 수 있다.")
    @Test
    void updateMemberInfo() {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("minju");
        MEMBER.updateMember("minju");

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(memberRepository.save(any()))
                .willReturn(MEMBER);

        // when
        memberService.updateMemberInfo(MEMBER.getId(), memberInfoRequest);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());
    }
}
