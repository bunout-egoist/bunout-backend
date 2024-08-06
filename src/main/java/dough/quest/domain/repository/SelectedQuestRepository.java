package dough.quest.domain.repository;

import dough.global.annotation.TimeTrace;
import dough.quest.domain.SelectedQuest;
import dough.quest.dto.CompletedCountDateElement;
import dough.quest.dto.TotalCompletedQuestsElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
            SELECT new dough.quest.dto.TotalCompletedQuestsElement(
                SUM(CASE WHEN q.questType = 'DAILY' OR q.questType = 'FIXED' THEN 1 ELSE 0 END),
                SUM(CASE WHEN q.questType = 'SPECIAL' THEN 1 ELSE 0 END)
            )
            FROM SelectedQuest sq
            LEFT JOIN Quest q ON sq.quest.id = q.id
            WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
           """)
    TotalCompletedQuestsElement getTotalCompletedQuestsByMemberId(@Param("memberId") final Long memberId);

    @TimeTrace
    @Query("""
            SELECT new dough.quest.dto.CompletedCountDateElement(
                sq.completedAt,
                SUM(CASE WHEN q.questType = 'DAILY' OR q.questType = 'FIXED' THEN 1 ELSE 0 END),
                SUM(CASE WHEN q.questType = 'SPECIAL' THEN 1 ELSE 0 END)
            )
            FROM SelectedQuest sq
            LEFT JOIN sq.quest q
            WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
                AND FUNCTION('YEAR', sq.completedAt) = :year
                AND FUNCTION('MONTH', sq.completedAt) = :month
            GROUP BY sq.completedAt
            ORDER BY sq.completedAt
           """)
    List<CompletedCountDateElement> getDateAndCompletedQuestsCountByMemberId(@Param("memberId") final Long memberId, @Param("year") Long year, @Param("month") Long month);

//    @Modifying
//    @Query("UPDATE SelectedQuest sq SET sq.feedback = :feedback, sq.questStatus = 'COMPLETED' WHERE sq.id = :selectedQuestId")
//    void updateFeedbackAndStatus(Long selectedQuestId, Feedback feedback);
}