package dough.quest.domain.repository;

import dough.quest.domain.SelectedQuest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedQuestRepository extends JpaRepository<SelectedQuest, Long> {
}
