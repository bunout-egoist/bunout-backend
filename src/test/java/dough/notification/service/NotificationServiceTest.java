package dough.notification.service;

import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.notification.dto.request.NotificationUpdateRequest;
import dough.notification.dto.request.NotificationsUpdateRequest;
import dough.notification.dto.response.NotificationResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.notification.domain.type.NotificationType.DAILY_QUEST;
import static dough.notification.fixture.notificationFixture.NotificationFixture.DAILY_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @DisplayName("멤버의 전체 알림을 조회할 수 있다.")
    @Test
    void getAllNotifications() {
        // given
        final List<Notification> notifications = List.of(DAILY_NOTIFICATION);

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(GOEUN));
        given(notificationRepository.findAllByMemberId(any()))
                .willReturn(notifications);

        // when
        final List<NotificationResponse> actualResponses = notificationService.getAllNotifications(GOEUN.getId());

        // then
        assertThat(actualResponses).usingRecursiveComparison()
                .isEqualTo(List.of(NotificationResponse.of(DAILY_NOTIFICATION)));
    }

    @DisplayName("알람을 업데이트 할 수 있다.")
    @Test
    void updateNotifications() {
        // given
        final Notification updatedNotification = new Notification(GOEUN, DAILY_QUEST);
        updatedNotification.changeIsChecked(false);

        final NotificationsUpdateRequest notificationsUpdateRequest = new NotificationsUpdateRequest(List.of(
                new NotificationUpdateRequest(DAILY_NOTIFICATION.getId(), false)
        ));

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(GOEUN));
        given(notificationRepository.findAllByMemberIdAndNotificationIds(anyLong(), any()))
                .willReturn(List.of(DAILY_NOTIFICATION));
        given(notificationRepository.saveAll(any()))
                .willReturn(List.of(updatedNotification));

        // when
        notificationService.updateNotifications(DAILY_NOTIFICATION.getId(), notificationsUpdateRequest);

        // then
        verify(memberRepository).findById(anyLong());
        verify(notificationRepository).findAllByMemberIdAndNotificationIds(anyLong(), any());
        verify(notificationRepository).saveAll(any());
    }
}
