package dough.quest.domain.repository;

import dough.quest.domain.CompletedQuest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedQuestRepository extends JpaRepository<CompletedQuest, Long> {
}
