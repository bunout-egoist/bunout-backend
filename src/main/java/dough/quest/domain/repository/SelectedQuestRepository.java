package dough.quest.domain.repository;

import dough.quest.domain.SelectedQuest;
import dough.quest.dto.CompletedQuestFeedbackElement;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

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
}
