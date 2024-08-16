package dough.notification.service;

import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.notification.dto.request.NotificationUpdateRequest;
import dough.notification.dto.request.NotificationsUpdateRequest;
import dough.notification.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static dough.global.exception.ExceptionCode.NOT_FOUND_NOTIFICATION_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getAllNotifications(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final List<Notification> notifications = notificationRepository.findAllByMemberId(member.getId());

        return getNotificationsResponse(notifications);
    }

    public List<NotificationResponse> updateNotifications(final Long memberId, final NotificationsUpdateRequest notificationsUpdateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final List<Notification> notifications = notificationRepository.findAllByMemberId(member.getId());

        final Map<Long, Boolean> isCheckedOfId = notificationsUpdateRequest.getNotifications().stream()
                .collect(Collectors.toMap(NotificationUpdateRequest::getId, NotificationUpdateRequest::getIsChecked));

        validateNotifications(isCheckedOfId.keySet(), notifications);

        final List<Notification> updatedNotifications = notifications.stream()
                .map(notification -> {
                    final Boolean isChecked = isCheckedOfId.get(notification.getId());
                    if (!notification.isSameIsChecked(isChecked)) {
                        notification.changeIsChecked(isChecked);
                        return notification;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final List<Notification> savedNotifications = notificationRepository.saveAll(updatedNotifications);
        return getNotificationsResponse(savedNotifications);
    }

    private List<NotificationResponse> getNotificationsResponse(final List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationResponse::of)
                .collect(Collectors.toList());
    }

    private void validateNotifications(final Set<Long> notificationIds, final List<Notification> notifications) {
        notifications.forEach(notification -> {
            if (!notificationIds.contains(notification.getId())) {
                throw new BadRequestException(NOT_FOUND_NOTIFICATION_ID);
            }
        });
    }
}
