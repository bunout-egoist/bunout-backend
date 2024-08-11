package dough.keyword.domain;

import dough.global.BaseEntity;
import dough.quest.domain.Quest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@RequiredArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE keyword SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isOutside;

    @Column(nullable = false)
    private Boolean isGroup;

    @OneToMany(mappedBy = "keyword")
    private List<Quest> quests = new ArrayList<>();
}
