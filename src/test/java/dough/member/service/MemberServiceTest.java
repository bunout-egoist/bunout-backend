package dough.member.service;

import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutTypeRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static dough.global.exception.ExceptionCode.ALREADY_UPDATED_BURNOUT_TYPE;
import static dough.global.exception.ExceptionCode.NOT_FOUND_QUEST_ID;
import static dough.member.fixture.MemberFixture.MEMBER;
import static dough.member.fixture.MemberFixture.UPDATED_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
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

    @DisplayName("멤버의 번아웃 유형을 수정할 수 있다.")
    @Test
    void changeBurnoutType() {
        // given
        final BurnoutTypeRequest burnoutTypeRequest = new BurnoutTypeRequest("호빵");

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(memberRepository.save(any()))
                .willReturn(UPDATED_MEMBER);

        // when
        memberService.changeBurnoutType(MEMBER.getId(), burnoutTypeRequest);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());
    }

    @DisplayName("번아웃 유형이 이번 달에 수정된 기록이 있을 경우 예외가 발생한다.")
    @Test
    void changeBurnoutType_AlreadyUpdatedBurnoutType() {
        // given
        final BurnoutTypeRequest burnoutTypeRequest = new BurnoutTypeRequest("호빵");

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));

        memberService.changeBurnoutType(MEMBER.getId(), burnoutTypeRequest);

        // when & then
        assertThatThrownBy(() -> memberService.validateBurnoutTypeUpdate(LocalDate.of(2024, 8, 1)))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_UPDATED_BURNOUT_TYPE.getCode());
    }
}
