package dough.quest.domain.repository;

import dough.quest.domain.SelectedQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(sq) > 0 THEN true ELSE false END 
            FROM SelectedQuest sq 
            WHERE sq.quest = :questId
            """)
    Boolean existsByQuestId(final Long questId);
}
