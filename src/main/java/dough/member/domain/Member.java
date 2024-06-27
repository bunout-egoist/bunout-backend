package dough.member.domain;

import dough.feedback.domain.QuestFeedback;
import dough.global.BaseEntity;
import dough.member.domain.type.GenderType;
import dough.member.domain.type.OccupationType;
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
    private List<QuestFeedback> questFeedbacks = new ArrayList<>();

    private String nickname;

    private OccupationType occupation;

    private GenderType gender;

    private Integer birthYear;

    private String burnoutType;

    private String role;

    private String email;

    private String socialLoginId;

    private SocialLoginType socialLoginType;

    private Integer level;

    private Integer experience;

    private Integer maxStreak;

    private LocalDateTime lastLoginDate;
}
