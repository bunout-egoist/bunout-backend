package dough.login.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.global.exception.UserNotFoundException;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.type.QuestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.member.fixture.MemberFixture.GOEUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class SignUpServiceTest {

    @InjectMocks
    private SignUpService signUpService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private BurnoutRepository burnoutRepository;

    @Mock
    private QuestRepository questRepository;

    private SignUpRequest signUpRequest;

    private Burnout burnout;

    private Quest quest;

    @BeforeEach
    void setup() {
        signUpRequest = new SignUpRequest("validAccessToken", "kimjunhee", "남성", 1990, "직장인", 1L, 1L);
        burnout = new Burnout(1L, "Burnout Type");
        quest = new Quest(1L, "Activity", "Description", QuestType.FIXED, 3, burnout, null);
    }

    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 수 있다.")
    @Test
    void updateMemberInfo() {
        // given
        given(tokenProvider.getMemberIdFromToken(anyString())).willReturn(1L);
        given(memberRepository.findMemberById(anyLong())).willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong())).willReturn(Optional.of(burnout));
        given(questRepository.findById(anyLong())).willReturn(Optional.of(quest));
        given(memberRepository.save(any(Member.class))).willReturn(GOEUN);

        // when
        MemberInfoResponse response = signUpService.updateMemberInfo(signUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(GOEUN.getId());
        assertThat(response.getNickname()).isEqualTo(GOEUN.getNickname());

        // save 메서드에 전달된 Member 객체 캡처
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(1)).save(memberCaptor.capture());
        Member capturedMember = memberCaptor.getValue();

        // then
        assertThat(capturedMember.getNickname()).isEqualTo(signUpRequest.getNickname());
        assertThat(capturedMember.getGender()).isEqualTo(signUpRequest.getGender());
        assertThat(capturedMember.getBirthYear()).isEqualTo(signUpRequest.getBirth_year());
        assertThat(capturedMember.getOccupation()).isEqualTo(signUpRequest.getOccupation());
        assertThat(capturedMember.getBurnout().getId()).isEqualTo(burnout.getId());
        assertThat(capturedMember.getQuest().getId()).isEqualTo(quest.getId());

        verify(tokenProvider, times(1)).getMemberIdFromToken(anyString());
        verify(memberRepository, times(1)).findMemberById(anyLong());
        verify(burnoutRepository, times(1)).findById(anyLong());
        verify(questRepository, times(1)).findById(anyLong());
    }

    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 회원을 찾지 못하면 예외가 발생한다.")
    @Test
    void updateMemberInfoUserNotFound() {
        // given
        given(tokenProvider.getMemberIdFromToken(anyString())).willReturn(1L);
        given(memberRepository.findMemberById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signUpService.updateMemberInfo(signUpRequest))
                .isInstanceOf(UserNotFoundException.class);

        verify(tokenProvider, times(1)).getMemberIdFromToken(anyString());
        verify(memberRepository, times(1)).findMemberById(anyLong());
        verify(burnoutRepository, times(0)).findById(anyLong());
        verify(questRepository, times(0)).findById(anyLong());
        verify(memberRepository, times(0)).save(any(Member.class));
    }

    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 번아웃 정보를 찾지 못하면 예외가 발생한다.")
    @Test
    void updateMemberInfoBurnoutNotFound() {
        // given
        given(tokenProvider.getMemberIdFromToken(anyString())).willReturn(1L);
        given(memberRepository.findMemberById(anyLong())).willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signUpService.updateMemberInfo(signUpRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("요청하신 ID에 해당하는 번아웃 유형을 찾을 수 없습니다.");

        verify(tokenProvider, times(1)).getMemberIdFromToken(anyString());
        verify(memberRepository, times(1)).findMemberById(anyLong());
        verify(burnoutRepository, times(1)).findById(anyLong());
        verify(questRepository, times(0)).findById(anyLong());
        verify(memberRepository, times(0)).save(any(Member.class));
    }

    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 고정 퀘스트 정보를 찾지 못하면 예외가 발생한다.")
    @Test
    void updateMemberInfoQuestNotFound() {
        // given
        given(tokenProvider.getMemberIdFromToken(anyString())).willReturn(1L);
        given(memberRepository.findMemberById(anyLong())).willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong())).willReturn(Optional.of(burnout));
        given(questRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signUpService.updateMemberInfo(signUpRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("요청하신 ID에 해당하는 퀘스트를 찾을 수 없습니다.");

        verify(tokenProvider, times(1)).getMemberIdFromToken(anyString());
        verify(memberRepository, times(1)).findMemberById(anyLong());
        verify(burnoutRepository, times(1)).findById(anyLong());
        verify(questRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(0)).save(any(Member.class));
    }
}
