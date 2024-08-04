package dough.quest.domain.repository;

import dough.quest.domain.SelectedQuest;
import dough.quest.dto.CompletedQuestFeedbackElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

    @Query("SELECT DISTINCT new dough.quest.dto.CompletedQuestFeedbackElement(s.quest, f) " +
            "FROM SelectedQuest s " +
            "LEFT JOIN s.feedback f " +
            "LEFT JOIN s.quest q " +
            "WHERE s.member.id = :memberId AND FUNCTION('DATE', s.createdAt) = :date AND s.questStatus = 'COMPLETED'")
    List<CompletedQuestFeedbackElement> findCompletedQuestFeedbackByMemberIdAndDate(
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

//    @Modifying
//    @Query("UPDATE SelectedQuest sq SET sq.feedback = :feedback, sq.questStatus = 'COMPLETED' WHERE sq.id = :selectedQuestId")
//    void updateFeedbackAndStatus(Long selectedQuestId, Feedback feedback);
}