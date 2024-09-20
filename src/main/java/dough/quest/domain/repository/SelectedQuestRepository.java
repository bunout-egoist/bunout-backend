package dough.quest.domain.repository;

import dough.quest.domain.Quest;
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

    Boolean existsByQuest(final Quest quest);

    @Query("""
             SELECT sq
             FROM SelectedQuest sq
             LEFT JOIN FETCH sq.quest q
             LEFT JOIN FETCH sq.member m
             LEFT JOIN FETCH q.keyword k
             LEFT JOIN FETCH sq.feedback f
             WHERE sq.questStatus = 'IN_PROGRESS' AND sq.member.id = :memberId
             AND sq.quest.questType = 'BY_TYPE' AND q.burnout.id = :burnoutId AND sq.dueDate < :date
            """)
    List<SelectedQuest> findIncompleteByTypeQuestsByMemberId(@Param("memberId") final Long memberId, @Param("burnoutId") final Long burnoutId, @Param("date") final LocalDate date);

    @Query("""
             SELECT sq
             FROM SelectedQuest sq
             LEFT JOIN FETCH sq.quest q
             LEFT JOIN FETCH q.keyword k
             LEFT JOIN FETCH sq.member m
             LEFT JOIN FETCH sq.feedback f
             WHERE m.id = :memberId AND sq.dueDate = :date
            """)
    List<SelectedQuest> findTodayQuests(@Param("memberId") final Long memberId, @Param("date") final LocalDate date);

    @Query("""
             SELECT new dough.quest.dto.CompletedQuestsTotalElement(
                 SUM(CASE WHEN q.questType = 'BY_TYPE' OR q.questType = 'FIXED' THEN 1 ELSE 0 END),
                 SUM(CASE WHEN q.questType = 'SPECIAL' THEN 1 ELSE 0 END)
             )
             FROM SelectedQuest sq
             LEFT JOIN sq.quest q
             LEFT JOIN q.keyword k
             WHERE sq.member.id = :memberId AND sq.questStatus = 'COMPLETED'
            """)
    CompletedQuestsTotalElement getCompletedQuestsTotalByMemberId(@Param("memberId") final Long memberId);

    @Query("""
            SELECT sq
            FROM SelectedQuest sq
            LEFT JOIN FETCH sq.quest q
            LEFT JOIN FETCH sq.feedback f
            WHERE q.questType = 'SPECIAL'
            """)
    Optional<SelectedQuest> findSpecialQuest();

    @Query("""
             SELECT new dough.quest.dto.CompletedQuestsCountElement(
                 sq.completedDate,
                 SUM(CASE WHEN q.questType = 'BY_TYPE' OR q.questType = 'FIXED' THEN 1 ELSE 0 END),
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
}