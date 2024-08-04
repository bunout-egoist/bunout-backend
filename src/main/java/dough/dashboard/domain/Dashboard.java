package dough.dashboard.domain;

import dough.member.domain.Member;
import dough.quest.domain.SelectedQuest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE dashboard SET status = 'DELETED' where id = ?")
public class Dashboard {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "dashboard")
    private List<SelectedQuest> selectedQuests = new ArrayList<>();

    private LocalDate completedAt;

    private Long fixedCount;

    private Long specialCount;

    private Long dailyCount;

    public Dashboard(
            final Long id,
            final Member member,
            final LocalDate completedAt,
            final Long fixedCount,
            final Long specialCount,
            final Long dailyCount
    ) {
        this.id = id;
        this.member = member;
        this.completedAt = completedAt;
        this.fixedCount = fixedCount;
        this.specialCount = specialCount;
        this.dailyCount = dailyCount;
    }
}
