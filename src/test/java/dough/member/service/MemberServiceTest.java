package dough.member.service;

import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.quest.domain.repository.QuestRepository;
import dough.member.dto.request.FixedQuestRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.ENTHUSIAST;
import static dough.global.exception.ExceptionCode.ALREADY_UPDATED_BURNOUT_TYPE;
import static dough.global.exception.ExceptionCode.ALREADY_UPDATED_FIXED_QUEST;
import static dough.member.fixture.MemberFixture.MEMBER;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

    @Mock
    private QuestRepository questRepository;

    @Mock
    private BurnoutRepository burnoutRepository;

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
        final BurnoutRequest burnoutRequest = new BurnoutRequest(1L);
        MEMBER.updateBurnout(ENTHUSIAST, LocalDate.of(2024, 7, 11));

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(memberRepository.save(any()))
                .willReturn(MEMBER);
        given(burnoutRepository.findById(any()))
                .willReturn(Optional.of(ENTHUSIAST));

        // when
        memberService.updateBurnout(MEMBER.getId(), burnoutRequest);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());
    }

    @DisplayName("번아웃 유형이 이번 달에 수정된 기록이 있을 경우 예외가 발생한다.")
    @Test
    void changeBurnoutType_AlreadyUpdatedBurnoutType() {
        // given
        final BurnoutRequest burnoutRequest = new BurnoutRequest(1L);

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(burnoutRepository.findById(any()))
                .willReturn(Optional.of(ENTHUSIAST));

        // when & then
        assertThatThrownBy(() -> memberService.updateBurnout(MEMBER.getId(), burnoutRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_UPDATED_BURNOUT_TYPE.getCode());
    }

    @DisplayName("멤버의 고정 퀘스트를 재설정할 수 있다.")
    @Test
    void changeFixedQuest() {
        // given
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(FIXED_QUEST1.getId());
        MEMBER.updateFixedQuest(FIXED_QUEST1, LocalDate.of(2024, 8, 3));

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(questRepository.findById(any()))
                .willReturn(Optional.of(FIXED_QUEST1));
        given(memberRepository.save(any()))
                .willReturn(MEMBER);

        // when
        memberService.updateFixedQuest(MEMBER.getId(), fixedQuestRequest);

        // then
        verify(memberRepository).findById(any());
        verify(questRepository).findById(any());
        verify(memberRepository).save(any());
    }

    @DisplayName("고정 퀘스트 유형이 이번 주에 수정된 기록이 있을 경우 예외가 발생한다.")
    @Test
    void changeFixedQuest_AlreadyUpdatedFixedQuestType() {
        // given
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(FIXED_QUEST1.getId());

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(questRepository.findById(any()))
                .willReturn(Optional.of(FIXED_QUEST1));

        // when & then
        assertThatThrownBy(() -> memberService.updateFixedQuest(MEMBER.getId(), fixedQuestRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_UPDATED_FIXED_QUEST.getCode());
    }
}
