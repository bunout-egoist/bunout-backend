package dough.quest.domain;

import dough.feedback.domain.Feedback;
import dough.global.BaseEntity;
import dough.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@RequiredArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE completed_quest SET status = 'DELETE' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class CompletedQuest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @OneToOne
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;
}
