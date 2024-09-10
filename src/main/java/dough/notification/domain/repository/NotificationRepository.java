package dough.notification.domain.repository;

import dough.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
             SELECT n
             FROM Notification n
             WHERE n.member.id = :memberId
            """)
    List<Notification> findAllByMemberId(@Param("memberId") final Long memberId);

    @Query("""
             SELECT n
             FROM Notification n
             WHERE n.member.id = :memberId AND n.id IN :notificationsId
            """)
    List<Notification> findAllByMemberIdAndNotificationIds(@Param("memberId") final Long memberId,  @Param("notificationsId") final Set<Long> notificationsId);
}
