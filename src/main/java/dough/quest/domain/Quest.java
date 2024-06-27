package dough.quest.domain;

import dough.global.BaseEntity;
import dough.quest.domain.type.QuestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@RequiredArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE quest SET status = 'DELETE' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class Quest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private QuestType questType;

    @Column(nullable = false)
    private Integer difficulty;

    @OneToMany(mappedBy = "quest")
    private List<CompletedQuest> completedQuests = new ArrayList<>();
}
