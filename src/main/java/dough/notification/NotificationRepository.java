package dough.notification;

import dough.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
             SELECT n
             FROM Notification n
             WHERE n.member.id = :memberId
            """)
    List<Notification> findAllByMemberId(@Param("memberId") final Long memberId);
}
