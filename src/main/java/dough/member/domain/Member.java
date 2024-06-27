package dough.member.domain;

import dough.feedback.domain.feedback;
import dough.global.BaseEntity;
import dough.member.domain.type.RoleType;
import dough.member.domain.type.SocialLoginType;
import dough.quest.domain.CompletedQuest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'DELETE' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<CompletedQuest> completedQuests = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<feedback> feedbacks = new ArrayList<>();

    @Column(length = 5)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private RoleType role;

    @Column(nullable = false)
    private String socialLoginId;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private SocialLoginType socialLoginType;

    private String email;

    private Integer level;

    private Integer experience;

    private Integer maxStreak;

    private String occupation;

    private String gender;

    private Integer birthYear;

    private String burnoutType;

    private LocalDateTime lastLoginDate;
}
