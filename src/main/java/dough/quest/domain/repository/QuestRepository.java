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

    @Query("""
            SELECT quest
            FROM Quest quest
            WHERE quest.burnout.id = :burnoutId AND quest.questType = 'FIXED'
           """)
    List<Quest> findFixedQuestsByBurnoutId(@Param("burnoutId") final Long burnoutId);

    @Query("""
            SELECT quest
            FROM Quest quest
            WHERE quest.questType = 'SPECIAL'
           """)
    List<Quest> findSpecialQuest();

    @Query("""
            SELECT q
            FROM Quest q
            LEFT JOIN q.selectedQuests sq ON q.id = sq.quest.id AND sq.member.id = :memberId
            LEFT JOIN FETCH q.keyword k
            WHERE q.questType = 'BY_TYPE' AND q.burnout.id = :burnoutId AND sq.quest.id IS NULL
           """)
    List<Quest> findTodayByTypeQuestsByMemberId(@Param("memberId") final Long memberId, @Param("burnoutId") final Long burnoutId);
}
