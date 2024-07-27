package dough.feedback.domain;

import dough.global.BaseEntity;
import dough.member.domain.Member;
import dough.quest.domain.SelectedQuest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE feedback SET status = 'DELETED' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(mappedBy = "feedback")
    private SelectedQuest selectedQuest;

    private String message;

    private String imageUrl;

    private Integer difficulty;

    public Feedback(
            final Long id,
            final Member member,
            final SelectedQuest selectedQuest,
            final String message,
            final String imageUrl,
            final Integer difficulty
    ) {
        this.id = id;
        this.member = member;
        this.selectedQuest = selectedQuest;
        this.message = message;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
    }
}
