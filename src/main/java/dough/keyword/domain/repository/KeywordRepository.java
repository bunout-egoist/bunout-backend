package dough.keyword.domain.repository;

import dough.keyword.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("""
        SELECT k, COUNT(sq)
        FROM Keyword k
        LEFT JOIN k.quests q
        LEFT JOIN q.selectedQuests sq
        WHERE q.questType = 'DAILY'
          AND q.difficulty = :level
          AND q.burnout.id = :burnoutId
          AND (sq.member.id = :memberId OR sq.id IS NULL)
        GROUP BY k.id, k.isOutside, k.isGroup
       """)
    List<Object[]> findKeywordAndCountSelectedQuests(
            @Param("level") Integer level,
            @Param("burnoutId") Long burnoutId,
            @Param("memberId") Long memberId);
}
