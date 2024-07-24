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

import static dough.member.fixture.MemberFixture.MEMBER1;
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
        given(memberRepository.findById(MEMBER1.getId()))
                .willReturn(Optional.of(MEMBER1));

        // when
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(MEMBER1.getId());

        // then
        assertThat(memberInfoResponse).usingRecursiveComparison().isEqualTo(MemberInfoResponse.from(MEMBER1));
    }

    @DisplayName("멤버의 닉네임을 수정할 수 있다.")
    @Test
    void updateMemberInfo() {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("minju");
        MEMBER1.updateMember("minju");

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER1));
        given(memberRepository.save(any()))
                .willReturn(MEMBER1);

        // when
        memberService.updateMemberInfo(MEMBER1.getId(), memberInfoRequest);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());
    }
}
