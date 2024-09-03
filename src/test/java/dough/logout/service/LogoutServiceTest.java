package dough.logout.service;

import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.logout.dto.request.DeleteAccessTokenRequest;
import dough.logout.dto.response.DeleteAccessTokenResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class LogoutServiceTest {
    @InjectMocks
    private LogoutService logoutService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    private String validAccessToken;
    private String invalidAccessToken;
    private Long memberId;
    private Long nonExistingMemberId;
    private Member member;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        validAccessToken = "validAccessToken";
        invalidAccessToken = "invalidAccessToken";
        memberId = 1L;
        nonExistingMemberId = 99L;
        member = MemberFixture.GOEUN;
        refreshToken = new RefreshToken(member, "refreshToken");
    }

    @Test
    public void logout() {
        // given
        DeleteAccessTokenRequest request = new DeleteAccessTokenRequest();
        request.setAccessToken(validAccessToken);

        when(tokenProvider.validToken(validAccessToken)).thenReturn(true);
        when(tokenProvider.getMemberIdFromToken(validAccessToken)).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(refreshTokenRepository.findByMemberId(memberId)).thenReturn(Optional.of(refreshToken));

        // when
        DeleteAccessTokenResponse response = logoutService.logout(request);

        // then
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
        assertEquals(member.getId(), response.getId());
        assertEquals(member.getNickname(), response.getNickname());
    }

    @Test
    public void logoutWithInvalidToken() {
        // given
        DeleteAccessTokenRequest request = new DeleteAccessTokenRequest();
        request.setAccessToken(invalidAccessToken);

        when(tokenProvider.validToken(invalidAccessToken)).thenReturn(false);

        // when & then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            logoutService.logout(request);
        });

        assertEquals("올바르지 않은 요청입니다.", exception.getMessage());
    }
    @Test
    public void logoutWithNonExistingMember() {
        // given
        DeleteAccessTokenRequest request = new DeleteAccessTokenRequest();
        request.setAccessToken(validAccessToken);

        // Mocking the token provider to return nonExistingMemberId for the validAccessToken
        when(tokenProvider.validToken(validAccessToken)).thenReturn(true);
        when(tokenProvider.getMemberIdFromToken(validAccessToken)).thenReturn(nonExistingMemberId);
        when(memberRepository.findById(nonExistingMemberId)).thenReturn(Optional.empty());

        // when & then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            logoutService.logout(request);
        });

        assertEquals("요청하신 ID에 해당하는 유저를 찾을 수 없습니다.", exception.getMessage());
    }

}
