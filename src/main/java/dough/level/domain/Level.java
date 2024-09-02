package dough.level.domain;

import dough.global.BaseEntity;
import dough.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@SQLDelete(sql = "UPDATE level SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Level extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Min(1)
    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer accumulatedExp;

    @Column(nullable = false)
    private Integer requiredExp;


    @OneToMany(mappedBy = "level")
    private List<Member> members = new ArrayList<>();

    public Level(
            final Integer level,
            final Integer accumulatedExp,
            final Integer requiredExp
            ) {
        this.level = level;
        this.accumulatedExp = accumulatedExp;
        this.requiredExp = requiredExp;
    }
}