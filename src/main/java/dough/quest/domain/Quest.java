package dough.quest.domain;

import dough.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

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

    private String content;

    private String questType;

    private Integer difficulty;

    @OneToMany(mappedBy = "quest")
    private List<CompletedQuest> completedQuests = new ArrayList<>();
}
