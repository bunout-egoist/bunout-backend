package dough.quest.domain.repository;

import dough.quest.domain.SelectedQuest;
import dough.quest.dto.CompletedQuestCountElement;
import dough.quest.dto.DateCompletedQuestCountElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

    @Query("""
             SELECT sq 
             FROM SelectedQuest sq
             LEFT JOIN FETCH  sq.feedback f
             LEFT JOIN FETCH sq.quest q
             WHERE sq.member.id = :memberId AND FUNCTION('DATE', sq.createdAt) = :date AND sq.questStatus = 'COMPLETED'
            """)
    List<SelectedQuest> findCompletedQuestsByMemberIdAndDate(
            @Param("memberId") final Long memberId,
            @Param("date") final LocalDate date
    );

    @Query("""
             SELECT CASE WHEN COUNT(sq) > 0 THEN true ELSE false END 
             FROM SelectedQuest sq 
             WHERE sq.quest.id = :questId
            """)
    Boolean existsByQuestId(final Long questId);

    Optional<SelectedQuest> findByQuestId(Long questId);

    @Query("""
            SELECT
                SUM(CASE WHEN q.questType = 'DAILY' OR q.questType = 'FIXED' THEN 1 ELSE 0 END) AS dailyAndFixedCount,
                SUM(CASE WHEN q.questType = 'SPECAIL' THEN 1 ELSE 0 END) AS specialCount
            FROM SelectedQuest sq
            LEFT JOIN sq.quest q
            WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
            """)
    CompletedQuestCountElement countTotalCompletedQuestsByMemberId(@Param("memberId") final Long memberId);

    //TODO 연월 비교 필요
    @Query("""
            SELECT 
                sq.createdAt,
                SUM(CASE WHEN q.questType = 'DAILY' OR q.questType = 'FIXED' THEN 1 ELSE 0 END) AS dailyAndFixedCount
            FROM SelectedQuest sq
            LEFT JOIN sq.quest q
            WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED' 
            GROUP BY sq.createdAt
            ORDER BY sq.createdAt
           """)
    List<DateCompletedQuestCountElement> getDateAndCompletedQuestsCountByMemberId(@Param("memberId") final Long memberId, @Param("year") Long year, @Param("month") Long month);

    // TODO 연월 비교 필요
    @Query("""
            SELECT sq.createdAt
                FROM SelectedQuest sq
                WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
            GROUP BY sq.createdAt
            HAVING COUNT(sq.createdAt) = 3
            ORDER BY sq.createdAt
        """)
    List<LocalDateTime> getCompletedThreeQuestsDate(@Param("memberId") final Long memberId, @Param("year") Long year, @Param("month") Long month);

//    @Modifying
//    @Query("UPDATE SelectedQuest sq SET sq.feedback = :feedback, sq.questStatus = 'COMPLETED' WHERE sq.id = :selectedQuestId")
//    void updateFeedbackAndStatus(Long selectedQuestId, Feedback feedback);
}