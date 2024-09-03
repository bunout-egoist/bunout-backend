package dough.notification.service;

import dough.global.exception.BadRequestException;
import dough.login.service.TokenService;
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
    private final TokenService tokenService;

    public List<NotificationResponse> getAllNotifications() {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));


        final List<Notification> notifications = notificationRepository.findAllByMemberId(member.getId());

        return getNotificationsResponse(notifications);
    }

    public List<NotificationResponse> updateNotifications(final NotificationsUpdateRequest notificationsUpdateRequest) {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));


        final Map<Long, Boolean> isCheckedOfId = notificationsUpdateRequest.getNotifications().stream()
                .collect(Collectors.toMap(NotificationUpdateRequest::getId, NotificationUpdateRequest::getIsChecked));

        final List<Notification> notifications = notificationRepository.findAllByMemberIdAndNotificationIds(member.getId(), isCheckedOfId.keySet());

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

        notificationRepository.saveAll(updatedNotifications);

        // TODO 더 좋은 방법이 없을지
        final List<Notification> savedNotifications = notificationRepository.findAllByMemberId(member.getId());

        return getNotificationsResponse(savedNotifications);
    }

    private List<NotificationResponse> getNotificationsResponse(final List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationResponse::of)
                .collect(Collectors.toList());
    }

    private void validateNotifications(final Set<Long> notificationIdsSet, final List<Notification> notifications) {
        final Set<Long> notificationIds = notifications.stream()
                .map(Notification::getId)
                .collect(Collectors.toSet());

        if (!notificationIds.containsAll(notificationIdsSet)) {
            throw new BadRequestException(NOT_FOUND_NOTIFICATION_ID);
        }
    }
}
