package dough.notification.domain;

import dough.global.BaseEntity;
import dough.member.domain.Member;
import dough.notification.domain.type.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE notification SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isChecked;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private NotificationType notificationType;

    public Notification(
            final Long id,
            final Member member,
            final NotificationType notificationType
    ) {
        this.id = id;
        this.member = member;
        this.isChecked = true;
        this.notificationType = notificationType;
    }

    public Notification(
            final Member member,
            final NotificationType notificationType
    ) {
        this(null, member, notificationType);
    }

    public void changeIsChecked() {
        this.isChecked = !isChecked;
    }
}
