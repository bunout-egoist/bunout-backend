package dough.quest.domain.repository;

import dough.quest.domain.SelectedQuest;
import dough.quest.dto.CompletedQuestFeedbackElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SelectedQuestRepository extends JpaRepository<SelectedQuest, Long> {

    @Query(value = "SELECT DISTINCT new dough.quest.dto.CompletedQuestFeedbackElement(s.quest, s.feedback) " +
            "FROM SelectedQuest s " +
            "LEFT JOIN FETCH s.feedback " +
            "LEFT JOIN FETCH s.quest " +
            "WHERE s.member.id = :memberId AND s.createdAt = :date AND s.questStatus = 'COMPLETED'", nativeQuery = true)
    List<CompletedQuestFeedbackElement> findCompletedQuestFeedbackByMemberIdAndDate(
            @Param("memberId") final Long memberId,
            @Param("date") final LocalDate date
    );
}
