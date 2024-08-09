package dough.burnout.domain.repository;

import dough.burnout.domain.Burnout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BurnoutRepository extends JpaRepository<Burnout, Long> {
}
