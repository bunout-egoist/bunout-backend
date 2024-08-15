package dough.notification.service;

import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.notification.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        return notifications.stream()
                .map(notification -> NotificationResponse.of(notification))
                .toList();
    }

    public void updateAllNotifications(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final List<Notification> notifications = notificationRepository.findAllByMemberId(member.getId());
        notifications.forEach(Notification::changeIsChecked);

        notificationRepository.saveAll(notifications);
    }

    public void update(final Long notificationId) {
        final Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_NOTIFICATION_ID));

        notification.changeIsChecked();
        notificationRepository.save(notification);
    }
}
