package dough.member.service;

import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.level.domain.MemberLevel;
import dough.level.service.LevelService;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.quest.domain.repository.QuestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.SOBORO;
import static dough.global.exception.ExceptionCode.ALREADY_UPDATED_BURNOUT_TYPE;
import static dough.global.exception.ExceptionCode.ALREADY_UPDATED_FIXED_QUEST;
import static dough.level.fixture.LevelFixture.LEVEL2;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private LevelService levelService;

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
        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.of(GOEUN));

        // when
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(GOEUN.getId());

        // then
        assertThat(memberInfoResponse).usingRecursiveComparison().isEqualTo(MemberInfoResponse.of(GOEUN));
    }

    @DisplayName("멤버의 닉네임을 수정할 수 있다.")
    @Test
    void updateMemberInfo() {
        // given
        final MemberInfoRequest memberInfoRequest = new MemberInfoRequest("minju");
        GOEUN.updateMember("minju");

        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(memberRepository.save(any()))
                .willReturn(GOEUN);

        // when
        memberService.updateMemberInfo(GOEUN.getId(), memberInfoRequest);

        // then
        verify(memberRepository).findMemberById(anyLong());
        verify(memberRepository).save(any());
    }

    @DisplayName("멤버의 번아웃 유형을 수정할 수 있다.")
    @Test
    void changeBurnoutType() {
        // given
        final BurnoutRequest burnoutRequest = new BurnoutRequest(1L);
        GOEUN.updateBurnout(SOBORO, LocalDate.of(2024, 7, 11));

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(memberRepository.save(any()))
                .willReturn(GOEUN);
        given(burnoutRepository.findById(anyLong()))
                .willReturn(Optional.of(SOBORO));

        // when
        memberService.updateBurnout(GOEUN.getId(), burnoutRequest);

        // then
        verify(memberRepository).findById(any());
        verify(memberRepository).save(any());
        verify(burnoutRepository).findById(anyLong());
    }

    @DisplayName("번아웃 유형이 이번 달에 수정된 기록이 있을 경우 예외가 발생한다.")
    @Test
    void changeBurnoutType_AlreadyUpdatedBurnoutType() {
        // given
        GOEUN.updateBurnout(SOBORO, LocalDate.now().withDayOfMonth(1));
        final BurnoutRequest burnoutRequest = new BurnoutRequest(1L);

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong()))
                .willReturn(Optional.of(SOBORO));

        // when & then
        assertThatThrownBy(() -> memberService.updateBurnout(GOEUN.getId(), burnoutRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_UPDATED_BURNOUT_TYPE.getCode());
    }

    @DisplayName("멤버의 고정 퀘스트를 재설정할 수 있다.")
    @Test
    void changeFixedQuest() {
        // given
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(FIXED_QUEST1.getId());
        GOEUN.updateFixedQuest(FIXED_QUEST1, LocalDate.of(2024, 8, 3));

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(questRepository.findById(anyLong()))
                .willReturn(Optional.of(FIXED_QUEST1));
        given(memberRepository.save(any()))
                .willReturn(GOEUN);

        // when
        memberService.updateFixedQuest(GOEUN.getId(), fixedQuestRequest);

        // then
        verify(memberRepository).findById(anyLong());
        verify(questRepository).findById(anyLong());
        verify(memberRepository).save(any());
    }

    @DisplayName("고정 퀘스트 유형이 이번 주에 수정된 기록이 있을 경우 예외가 발생한다.")
    @Test
    void changeFixedQuest_AlreadyUpdatedFixedQuestType() {
        // given
        final FixedQuestRequest fixedQuestRequest = new FixedQuestRequest(FIXED_QUEST1.getId());
        GOEUN.updateFixedQuest(FIXED_QUEST1, LocalDate.now());

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(questRepository.findById(anyLong()))
                .willReturn(Optional.of(FIXED_QUEST1));

        // when & then
        assertThatThrownBy(() -> memberService.updateFixedQuest(GOEUN.getId(), fixedQuestRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_UPDATED_FIXED_QUEST.getCode());
    }

    @DisplayName("출석체크를 할 수 있다.")
    @Test
    void checkAttendance() {
        // given
        GOEUN.updateAttendance(LocalDateTime.now().minusDays(7), 2, 5);

        final MemberLevel memberLevel = new MemberLevel(GOEUN, LEVEL2, true, 30);

        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(memberRepository.save(any()))
                .willReturn(GOEUN);
        given(levelService.updateLevel(any()))
                .willReturn(memberLevel);

        // when
        memberService.checkAttendance(GOEUN.getId());

        // then
        verify(memberRepository).findMemberById(anyLong());
        verify(memberRepository).save(any());
        verify(levelService).updateLevel(any());
    }
}
