package dough.quest.domain.repository;

import dough.global.annotation.TimeTrace;
import dough.quest.domain.SelectedQuest;
import dough.quest.dto.CompletedQuestElement;
import dough.quest.dto.CompletedQuestsCountElement;
import dough.quest.dto.CompletedQuestsTotalElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

    @Query("""
             SELECT new dough.quest.dto.CompletedQuestElement(q, f.imageUrl, sq.completedDate)
             FROM SelectedQuest sq
             LEFT JOIN sq.feedback f
             LEFT JOIN sq.quest q
             WHERE sq.member.id = :memberId AND sq.completedDate BETWEEN :startDate AND :endDate AND sq.questStatus = 'COMPLETED'
            """)
    List<CompletedQuestElement> findCompletedQuestsByMemberIdAndDate(@Param("memberId") final Long memberId, @Param("startDate") final LocalDate startDate, @Param("endDate") final LocalDate endDate);

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
             WHERE sq.status <> 'COMPLETED' AND sq.member.id = :memberId AND sq.quest.questType = 'DAILY' AND sq.dueDate = :date
            """)
    List<SelectedQuest> findIncompleteDailyQuestsByMemberIdAndDate(@Param("memberId") final Long memberId, @Param("date") final LocalDate date);

    @Query("""
             SELECT sq
             FROM SelectedQuest sq
             JOIN FETCH sq.quest
             JOIN FETCH sq.member
             WHERE sq.status <> 'COMPLETED' AND sq.member.id = :memberId AND sq.dueDate = :date
            """)
    List<SelectedQuest> findTodayDailyQuests(@Param("memberId") final Long memberId, @Param("date") final LocalDate date);

    Optional<SelectedQuest> findByQuestId(Long questId);

    @Query("""
             SELECT new dough.quest.dto.CompletedQuestsTotalElement(
                 SUM(CASE WHEN q.questType = 'DAILY' OR q.questType = 'FIXED' THEN 1 ELSE 0 END),
                 SUM(CASE WHEN q.questType = 'SPECIAL' THEN 1 ELSE 0 END)
             )
             FROM SelectedQuest sq
             LEFT JOIN sq.quest q
             WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
            """)
    CompletedQuestsTotalElement getCompletedQuestsTotalByMemberId(@Param("memberId") final Long memberId);

    @TimeTrace
    @Query("""
             SELECT new dough.quest.dto.CompletedQuestsCountElement(
                 sq.completedDate,
                 SUM(CASE WHEN q.questType = 'DAILY' OR q.questType = 'FIXED' THEN 1 ELSE 0 END),
                 SUM(CASE WHEN q.questType = 'SPECIAL' THEN 1 ELSE 0 END)
             )
             FROM SelectedQuest sq
             LEFT JOIN sq.quest q
             WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
                 AND FUNCTION('YEAR', sq.completedDate) = :year
                 AND FUNCTION('MONTH', sq.completedDate) = :month
             GROUP BY sq.completedDate
             ORDER BY sq.completedDate
            """)
    List<CompletedQuestsCountElement> getCompletedQuestsCountByMemberIdAndDate(@Param("memberId") final Long memberId, @Param("year") final int year, @Param("month") final int month);

//    @Modifying
//    @Query("UPDATE SelectedQuest sq SET sq.feedback = :feedback, sq.questStatus = 'COMPLETED' WHERE sq.id = :selectedQuestId")
//    void updateFeedbackAndStatus(Long selectedQuestId, Feedback feedback);
}