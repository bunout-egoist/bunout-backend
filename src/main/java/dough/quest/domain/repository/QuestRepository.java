package dough.quest.domain.repository;

import dough.quest.domain.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    @Modifying
    @Query("""
            UPDATE Quest quest
            SET quest.status = 'DELETED'
            WHERE quest.id = :questId
            """)
    void deleteByQuestId(@Param("questId") final Long questId);
}
