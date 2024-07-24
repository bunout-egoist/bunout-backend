package dough.member.domain;

import dough.feedback.domain.Feedback;
import dough.global.BaseEntity;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.quest.domain.SelectedQuest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
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
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'DELETED' where id = ?")
@SQLRestriction("status is 'ACTIVE'")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<SelectedQuest> selectedQuests = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Feedback> Feedbacks = new ArrayList<>();

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

    private LocalDateTime questLastModified;

    private LocalDateTime lastLogin;

    public Member(final Long id,
                  final String nickname,
                  final String socialLoginId,
                  final SocialLoginType socialLoginType,
                  final String email,
                  final String occupation,
                  final String gender,
                  final Integer birthYear,
                  final String burnoutType
    ) {
        this.id = id;
        this.nickname = nickname;
        this.socialLoginId = socialLoginId;
        this.socialLoginType = socialLoginType;
        this.email = email;
        this.level = 0;
        this.experience = 0;
        this.maxStreak = 0;
        this.occupation = occupation;
        this.gender = gender;
        this.birthYear = birthYear;
        this.burnoutType = burnoutType;
        this.questLastModified = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }

    public void updateMember(final String nickname) {
        this.nickname = nickname;
    }
}
