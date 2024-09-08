package dough.signout.service;

import dough.feedback.domain.Feedback;
import dough.feedback.domain.repository.FeedbackRepository;
import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.SelectedQuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class SignoutServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private SignoutService signoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignout_InvalidToken() {
        // Given
        String invalidToken = "invalidToken";
        when(tokenProvider.validToken(invalidToken)).thenReturn(false);

        // When
        SignoutRequestDTO signoutRequestDTO = new SignoutRequestDTO(invalidToken);

        // Then
        assertThrows(BadRequestException.class, () -> signoutService.signout(signoutRequestDTO));
        verify(memberRepository, never()).findById(anyLong());
    }

    @Test
    void testSignout_Successful() {
        // Given
        String testToken = "validToken123";
        Long memberId = 1L;

        Member member = mock(Member.class);
        when(member.getId()).thenReturn(memberId);
        when(member.getSelectedQuests()).thenReturn(Collections.emptyList());
        when(member.getFeedbacks()).thenReturn(Collections.emptyList());
        when(member.getNotifications()).thenReturn(Collections.emptyList());

        when(tokenProvider.validToken(testToken)).thenReturn(true);
        when(tokenProvider.getMemberIdFromToken(testToken)).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        SignoutRequestDTO signoutRequestDTO = new SignoutRequestDTO(testToken);
        SignoutResponseDTO signoutResponseDTO = signoutService.signout(signoutRequestDTO);

        // Then
        verify(memberRepository).delete(member);
        verify(selectedQuestRepository, never()).delete(any(SelectedQuest.class));
        verify(feedbackRepository, never()).delete(any(Feedback.class));
        verify(notificationRepository, never()).delete(any(Notification.class));

        assertEquals(memberId, signoutResponseDTO.getMemberId());
    }

    @Test
    void testSignout_MemberNotFound() {
        // Given
        String testToken = "validToken123";
        Long memberId = 1L;

        when(tokenProvider.validToken(testToken)).thenReturn(true);
        when(tokenProvider.getMemberIdFromToken(testToken)).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When
        SignoutRequestDTO signoutRequestDTO = new SignoutRequestDTO(testToken);

        // Then
        assertThrows(BadRequestException.class, () -> signoutService.signout(signoutRequestDTO));
        verify(memberRepository).findById(memberId);
    }
}