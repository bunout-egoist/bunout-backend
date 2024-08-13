package dough.notification.service;

import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.notification.dto.response.NotificationResponse;
import dough.quest.domain.Quest;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.response.QuestResponse;
import dough.quest.service.QuestService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dough.member.fixture.MemberFixture.MEMBER;
import static dough.notification.domain.type.NotificationType.DAILY_QUEST;
import static dough.notification.fixture.notificationFixture.NotificationFixture.*;
import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
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
                .willReturn(Optional.of(MEMBER));
        given(notificationRepository.findAllByMemberId(any()))
                .willReturn(notifications);

        // when
        final List<NotificationResponse> actualResponses = notificationService.getAllNotifications(MEMBER.getId());

        // then
        assertThat(actualResponses).usingRecursiveComparison()
                .isEqualTo(List.of(NotificationResponse.of(DAILY_NOTIFICATION)));
    }

    @DisplayName("단일 알람을 업데이트 할 수 있다.")
    @Test
    void update() {
        // given
        final Notification updatedNotification = new Notification(MEMBER, DAILY_QUEST);
        updatedNotification.changeIsChecked();

        given(notificationRepository.findById(anyLong()))
                .willReturn(Optional.of(DAILY_NOTIFICATION));
        given(notificationRepository.save(any()))
                .willReturn(updatedNotification);

        // when
        notificationService.update(DAILY_NOTIFICATION.getId());

        // then
        verify(notificationRepository).findById(anyLong());
        verify(notificationRepository).save(any());
    }

    @DisplayName("모든 알람을 업데이트 할 수 있다.")
    @Test
    void updateAllNotifications() {
        // given
        final Notification updatedNotification = new Notification(MEMBER, DAILY_QUEST);
        updatedNotification.changeIsChecked();

        given(memberRepository.findById(any()))
                .willReturn(Optional.of(MEMBER));
        given(notificationRepository.findAllByMemberId(anyLong()))
                .willReturn(List.of(DAILY_NOTIFICATION));
        given(notificationRepository.saveAll(any()))
                .willReturn(List.of(updatedNotification));

        // when
        notificationService.updateAllNotifications(MEMBER.getId());

        // then
        verify(memberRepository).findById(anyLong());
        verify(notificationRepository).findAllByMemberId(anyLong());
        verify(notificationRepository).saveAll(any());
    }
}
