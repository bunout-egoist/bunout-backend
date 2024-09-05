package dough.login.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.global.exception.UserNotFoundException;
import dough.level.domain.Level;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SignUpServiceTest {

    @InjectMocks
    private SignUpService signUpService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BurnoutRepository burnoutRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private NotificationRepository notificationRepository;

    private SignUpRequest signUpRequest;
    private Member member;
    private Burnout burnout;
    private Quest quest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mock data
        signUpRequest = new SignUpRequest("token", "kim", "남자", 2002, "기타", 1L, 1L);
        member = mock(Member.class);
        burnout = mock(Burnout.class);
        quest = mock(Quest.class);
        Level level = mock(Level.class);  // Level 객체 추가

        // Mock member's burnout, quest, and level
        when(member.getBurnout()).thenReturn(burnout);
        when(burnout.getName()).thenReturn("호빵");

        when(member.getQuest()).thenReturn(quest);  // quest 필드 설정
        when(quest.getId()).thenReturn(1L);

        when(member.getLevel()).thenReturn(level);  // level 필드 설정
        when(level.getLevel()).thenReturn(1);  // 필요한 경우 level의 level 값 설정
    }

    @Test
    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 수 있다.")
    void updateMemberInfo_SuccessfulUpdate() {
        when(tokenProvider.getMemberIdFromToken(signUpRequest.getAccessToken())).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(burnoutRepository.findById(signUpRequest.getBunoutId())).thenReturn(Optional.of(burnout));
        when(questRepository.findById(signUpRequest.getFixedQuestId())).thenReturn(Optional.of(quest));
        when(memberRepository.save(member)).thenReturn(member);

        signUpService.updateMemberInfo(signUpRequest);

        // Verify interactions
        verify(member).updateMember(signUpRequest.getNickname(), signUpRequest.getGender(),
                signUpRequest.getBirth_year(), signUpRequest.getOccupation());
        verify(burnoutRepository).findById(signUpRequest.getBunoutId());
        verify(questRepository).findById(signUpRequest.getFixedQuestId());
        verify(notificationRepository).saveAll(anyList());
        verify(memberRepository).save(member);
    }


    @Test
    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 회원을 찾지 못하면 예외가 발생한다.")
    void updateMemberInfo_UserNotFound() {
        when(tokenProvider.getMemberIdFromToken(signUpRequest.getAccessToken())).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> signUpService.updateMemberInfo(signUpRequest));

        verify(memberRepository).findById(1L);
        verify(member, never()).updateMember(anyString(), anyString(), anyInt(), anyString());
    }

    @Test
    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 번아웃 정보를 찾지 못하면 예외가 발생한다.")
    void updateMemberInfo_BurnoutNotFound() {
        when(tokenProvider.getMemberIdFromToken(signUpRequest.getAccessToken())).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(burnoutRepository.findById(signUpRequest.getBunoutId())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> signUpService.updateMemberInfo(signUpRequest));

        verify(burnoutRepository).findById(signUpRequest.getBunoutId());
    }

    @Test
    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 고정 퀘스트 정보를 찾지 못하면 예외가 발생한다.")
    void updateMemberInfo_QuestNotFound() {
        when(tokenProvider.getMemberIdFromToken(signUpRequest.getAccessToken())).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(burnoutRepository.findById(signUpRequest.getBunoutId())).thenReturn(Optional.of(burnout));
        when(questRepository.findById(signUpRequest.getFixedQuestId())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> signUpService.updateMemberInfo(signUpRequest));

        verify(questRepository).findById(signUpRequest.getFixedQuestId());
    }
}
