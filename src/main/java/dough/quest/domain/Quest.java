package dough.quest.domain;

import dough.global.BaseEntity;
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
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@RequiredArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE quest SET status = 'DELETED' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class Quest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String activity;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private QuestType questType;

    @Column(nullable = false)
    private Integer difficulty;

    @OneToMany(mappedBy = "quest")
    private List<SelectedQuest> selectedQuests = new ArrayList<>();

    public Quest (final Long id,
                  final String description,
                  final String activity,
                  final QuestType questType,
                  final Integer difficulty
    ) {
        this.id = id;
        this.description = description;
        this.activity = activity;
        this.questType = questType;
        this.difficulty = difficulty;
    }
}
