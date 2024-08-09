package dough.quest.domain;

import dough.feedback.domain.Feedback;
import dough.global.BaseEntity;
import dough.member.domain.Member;
import dough.quest.domain.type.QuestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

import static dough.quest.domain.type.QuestStatus.IN_PROGRESS;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@RequiredArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE selected_quest SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class SelectedQuest extends BaseEntity {

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

    @Enumerated(value = STRING)
    private QuestStatus questStatus = IN_PROGRESS;

    private LocalDate dueDate;

    public SelectedQuest(
            final Long id,
            final Member member,
            final Quest quest,
            final Feedback feedback
    ) {
        this.id = id;
        this.member = member;
        this.quest = quest;
        this.feedback = feedback;
        this.dueDate = LocalDate.now();
    }

    public SelectedQuest(
            final Member member,
            final Quest quest
    ) {
        this(null, member, quest, null);
    }

    public void updateDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void AddFeedbackToSelectedQuest(final Feedback feedback) {
        this.feedback = feedback;
        this.questStatus = QuestStatus.COMPLETED;
    }
}
