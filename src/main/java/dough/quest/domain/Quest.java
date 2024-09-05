package dough.quest.domain;

import dough.burnout.domain.Burnout;
import dough.global.BaseEntity;
import dough.keyword.domain.Keyword;
import dough.member.domain.Member;
import dough.quest.domain.type.QuestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@RequiredArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE quest SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Quest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String activity;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private QuestType questType;

    @Column(nullable = false)
    private Integer difficulty;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "burnout_id", nullable = false)
    private Burnout burnout;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @OneToMany(mappedBy = "quest")
    private List<SelectedQuest> selectedQuests = new ArrayList<>();

    @OneToMany(mappedBy = "quest")
    private List<Member> members = new ArrayList<>();

    public Quest(
            final Long id,
            final String activity,
            final String description,
            final QuestType questType,
            final Integer difficulty,
            final Burnout burnout,
            final Keyword keyword
    ) {
        this.id = id;
        this.activity = activity;
        this.description = description;
        this.questType = questType;
        this.difficulty = difficulty;
        this.burnout = burnout;
        this.keyword = keyword;
    }

    public Quest(
            final String activity,
            final String description,
            final QuestType questType,
            final Integer difficulty,
            final Burnout burnout,
            final Keyword keyword
    ) {
        this(null, activity, description, questType, difficulty, burnout, keyword);
    }
}
