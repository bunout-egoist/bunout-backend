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

    @Modifying
    @Query("""
            SELECT quest
            FROM Quest quest
            WHERE quest.burnout.id = :burnoutId AND quest.questType = 'FIXED'
           """)
    List<Quest> findFixedQuestsByBurnoutId(@Param("burnoutId") final Long burnoutId);

    @Modifying
    @Query("""
            SELECT quest
            FROM Quest quest
            WHERE quest.burnout.id = :burnoutId AND quest.questType = 'SPECIAL'
           """)
    List<Quest> findSpecialQuestByBurnoutId(@Param("burnoutId") final Long burnoutId);

    @Query("""
            SELECT q
            FROM Quest q
            LEFT JOIN FETCH q.selectedQuests sq
            LEFT JOIN FETCH q.keyword k
            WHERE q.questType = 'BY_TYPE' AND q.difficulty = :level AND q.burnout.id = :burnoutId
            AND (sq.id IS NULL OR sq.member.id = :memberId)
            ORDER BY q.difficulty ASC
           """)
    List<Quest> findTodayBY_TYPEQuestsByMemberId(@Param("memberId") final Long memberId, @Param("level") final Integer level, @Param("burnoutId") final Long burnoutId);
}
