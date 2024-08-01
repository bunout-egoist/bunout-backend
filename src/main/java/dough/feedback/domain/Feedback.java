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
@SQLRestriction("status = 'ACTIVE'")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(mappedBy = "feedback")
    private SelectedQuest selectedQuest;

    private String imageUrl;

    private Integer difficulty;
}
