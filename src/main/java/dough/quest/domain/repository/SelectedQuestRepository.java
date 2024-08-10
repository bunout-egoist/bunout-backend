package dough.quest.domain.repository;

import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

    @Query("""
             SELECT s FROM SelectedQuest s
             LEFT JOIN FETCH  s.feedback f
             LEFT JOIN FETCH s.quest q
             WHERE s.member.id = :memberId AND FUNCTION('DATE', s.createdAt) = :date AND s.questStatus = 'COMPLETED'
            """)
    List<SelectedQuest> findCompletedQuestByMemberIdAndDate(
            @Param("memberId") final Long memberId,
            @Param("date") final LocalDate date
    );

    @Query("""
             SELECT CASE WHEN COUNT(sq) > 0 THEN true ELSE false END 
             FROM SelectedQuest sq 
             WHERE sq.quest.id = :questId
            """)
    Boolean existsByQuestId(final Long questId);

    @Query("""
             SELECT sq
             FROM SelectedQuest sq
             JOIN FETCH sq.quest
             JOIN FETCH sq.member
             WHERE sq.status <> 'COMPLETED' AND sq.member.id = :memberId AND sq.quest.questType = 'DAILY' AND function('DATE', sq.dueDate) = :date
            """)
    List<SelectedQuest> findIncompletedDailyQuestsByMemberIdAndDate(@Param("memberId") final Long memberId, @Param("date") final LocalDate date);

    @Query("""
             SELECT sq
             FROM SelectedQuest sq
             JOIN FETCH sq.quest
             JOIN FETCH sq.member
             WHERE sq.status <> 'COMPLETED' AND sq.member.id = :memberId
            """)
    List<SelectedQuest> findTodayDailyQuests(@Param("memberId") final Long memberId);

    Optional<SelectedQuest> findByQuestId(Long questId);

//    @Modifying
//    @Query("UPDATE SelectedQuest sq SET sq.feedback = :feedback, sq.questStatus = 'COMPLETED' WHERE sq.id = :selectedQuestId")
//    void updateFeedbackAndStatus(Long selectedQuestId, Feedback feedback);
}