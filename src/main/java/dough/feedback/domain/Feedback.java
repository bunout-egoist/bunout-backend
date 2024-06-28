package dough.feedback.domain;

import dough.global.BaseEntity;
import dough.member.domain.Member;
import dough.quest.domain.CompletedQuest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE feedback SET status = 'DELETE' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(mappedBy = "feedback")
    private CompletedQuest completedQuest;

    private String message;

    private String imageUrl;

    private Integer difficulty;
}
