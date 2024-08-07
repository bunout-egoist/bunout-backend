package dough.burnout.domain;

import dough.global.BaseEntity;
import dough.member.domain.Member;
import dough.quest.domain.Quest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE burnout SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Burnout extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "burnout")
    private List<Quest> quests = new ArrayList<>();

    @OneToMany(mappedBy = "burnout")
    private List<Member> members = new ArrayList<>();

    public Burnout(
            final Long id,
            final String name
    ) {
        this.id = id;
        this.name = name;
    }
}
