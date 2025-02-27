package dough.member.domain;

import dough.burnout.domain.Burnout;
import dough.feedback.domain.Feedback;
import dough.global.BaseEntity;
import dough.level.domain.Level;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.notification.domain.Notification;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'DELETED' where id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "burnout_id")
    private Burnout burnout;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

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

    private String occupation;

    private String gender;

    private Integer birthYear;

    private String refreshToken;

    private String appleToken;

    private String notificationToken;

    @Column(nullable = false)
    private Integer exp;

    @Column(nullable = false)
    private LocalDate burnoutLastModified;

    @Column(nullable = false)
    private LocalDate fixedQuestLastModified;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private LocalDateTime attendanceAt;

    @Max(7)
    @Column(nullable = false)
    private Integer attendanceCount;

    @OneToMany(mappedBy = "member", cascade = REMOVE, orphanRemoval = true)
    private List<SelectedQuest> selectedQuests = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = REMOVE, orphanRemoval = true)
    private List<Feedback> Feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = REMOVE, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public Member(
            final Long id,
            final String nickname,
            final String socialLoginId,
            final SocialLoginType socialLoginType,
            final String occupation,
            final String gender,
            final Integer birthYear,
            final Burnout burnout,
            final RoleType roleType,
            final Level level,
            final Quest quest,
            final String appleToken
    ) {
        this.id = id;
        this.nickname = nickname;
        this.role = roleType;
        this.socialLoginId = socialLoginId;
        this.socialLoginType = socialLoginType;
        this.exp = 0;
        this.occupation = occupation;
        this.gender = gender;
        this.birthYear = birthYear;
        this.burnout = burnout;
        this.burnoutLastModified = LocalDate.EPOCH;
        this.fixedQuestLastModified = LocalDate.EPOCH;
        this.lastLogin = LocalDateTime.now();
        this.attendanceAt = LocalDate.EPOCH.atStartOfDay();
        this.attendanceCount = 0;
        this.level = level;
        this.quest = quest;
        this.appleToken = appleToken;
    }

    public Member(
            final String socialLoginId,
            final SocialLoginType socialLoginType,
            final RoleType roleType,
            final Level level
    ) {
        this(null, null, socialLoginId, socialLoginType, null, null, null, null, roleType, level, null, null);
    }

    public Member(
            final String socialLoginId,
            final SocialLoginType socialLoginType,
            final RoleType roleType,
            final Level level,
            final String appleToken
    ) {
        this(null, null, socialLoginId, socialLoginType, null, null, null, null, roleType, level, null, appleToken);
    }

    public void updateRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNotificationToken(final String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public void updateMember(
            final String nickname,
            final String gender,
            final Integer birthYear,
            final String occupation,
            final Burnout burnout,
            final Quest quest
    ) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthYear = birthYear;
        this.occupation = occupation;
        this.burnout = burnout;
        this.quest = quest;
        this.burnoutLastModified = LocalDate.now();
        this.fixedQuestLastModified = LocalDate.now();
    }

    public void updateMember(final String nickname) {
        this.nickname = nickname;
    }

    public void updateBurnout(
            final Burnout burnout,
            final LocalDate burnoutLastModified
    ) {
        this.burnout = burnout;
        this.burnoutLastModified = burnoutLastModified;
    }

    public void updateFixedQuest(
            final Quest quest,
            final LocalDate fixedQuestLastModified
    ) {
        this.quest = quest;
        this.fixedQuestLastModified = fixedQuestLastModified;
    }

    public void updateAttendance(
            final LocalDateTime attendanceAt,
            final Integer attendanceCount,
            final Integer exp
    ) {
        this.attendanceAt = attendanceAt;
        this.attendanceCount = attendanceCount;
        this.exp = exp;
    }

    public void updateExp(
            final Integer exp
    ) {
        this.exp = exp;
    }

    public void updateLevel(
            final Level level
    ) {
        this.level = level;
    }
}
