package dough.quest.domain.repository;

import dough.feedback.domain.Feedback;
import dough.quest.domain.SelectedQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

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
