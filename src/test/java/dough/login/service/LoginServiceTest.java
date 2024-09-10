package dough.login.service;

import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.login.dto.request.SignUpRequest;
import dough.login.infrastructure.jwt.TokenExtractor;
import dough.login.infrastructure.jwt.TokenProvider;
import dough.member.domain.repository.MemberRepository;
import dough.notification.domain.repository.NotificationRepository;
import dough.quest.domain.repository.QuestRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.SOBORO;
import static dough.global.exception.ExceptionCode.*;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.notification.fixture.notificationFixture.NotificationFixture.BY_TYPE_NOTIFICATION;
import static dough.quest.fixture.QuestFixture.FIXED_QUEST1;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenExtractor tokenExtractor;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private BurnoutRepository burnoutRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private NotificationRepository notificationRepository;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest(
                "nickname",
                "남자",
                2002,
                "기타",
                1L,
                1L
        );
    }

    @DisplayName("멤버의 추가 회원가입을 진행할 수 있다.")
    @Test
    void addtionalSignup() {
        // given
        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong()))
                .willReturn(Optional.of(SOBORO));
        given(questRepository.findById(anyLong()))
                .willReturn(Optional.of(FIXED_QUEST1));
        given(notificationRepository.saveAll(any()))
                .willReturn(List.of(BY_TYPE_NOTIFICATION));
        given(memberRepository.save(any()))
                .willReturn(GOEUN);

        // when
        loginService.completeSignup(GOEUN.getId(), signUpRequest);

        // then
        verify(memberRepository).findMemberById(anyLong());
        verify(burnoutRepository).findById(anyLong());
        verify(questRepository).findById(anyLong());
        verify(notificationRepository).saveAll(any());
        verify(memberRepository).save(any());
    }


    @DisplayName("추가 회원가입 시 멤버를 찾지 못하면 예외가 발생한다.")
    @Test
    void completeSignup_UserNotFound() {
        // given
        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.completeSignup(GOEUN.getId(), signUpRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_MEMBER_ID.getCode());
    }

    @DisplayName("추가 회원가입 시 번아웃 정보를 찾지 못하면 예외가 발생한다.")
    @Test
    void completeSignup_BurnoutNotFound() {
        // given
        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.completeSignup(GOEUN.getId(), signUpRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_BURNOUT_ID.getCode());
    }

    @Test
    @DisplayName("추가 회원가입 시 고정 퀘스트 정보를 찾지 못하면 예외가 발생한다.")
    void completeSignup_QuestNotFound() {
        // given
        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong()))
                .willReturn(Optional.of(SOBORO));
        given(questRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.completeSignup(GOEUN.getId(), signUpRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_QUEST_ID.getCode());
    }

    @DisplayName("액세스 토큰을 재발급 받을 수 있다.")
    @Test
    void renewAccessToken() {
        // given
        GOEUN.updateRefreshToken("Refresh Token");

        given(tokenExtractor.getRefreshToken())
                .willReturn("Refresh Token");
        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.of(GOEUN));
        given(tokenProvider.generateAccessToken(GOEUN.getId().toString()))
                .willReturn("Access Token");

        // when
        loginService.renewAccessToken(GOEUN.getId());

        // then
        verify(tokenExtractor).getRefreshToken();
        verify(memberRepository).findMemberById(anyLong());
        verify(tokenProvider).generateAccessToken(anyString());
    }

    @Test
    @DisplayName("멤버는 로그아웃을 할 수 있다.")
    void logout() {
        // given
        GOEUN.updateRefreshToken("Refresh Token");

        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.of(GOEUN));

        // when
        loginService.logout(GOEUN.getId());

        // then
        verify(memberRepository).findMemberById(anyLong());
    }

    @Test
    @DisplayName("멤버는 탈퇴를 할 수 있다.")
    void signout() throws IOException {
        // given
        GOEUN.updateRefreshToken("Refresh Token");

        given(memberRepository.findMemberById(GOEUN.getId()))
                .willReturn(Optional.of(GOEUN));

        // when
        loginService.signout(GOEUN.getId());

        // then
        verify(memberRepository).findMemberById(anyLong());
    }
}
