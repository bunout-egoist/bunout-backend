package dough.burnout.domain.repository;

import dough.burnout.domain.Burnout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BurnoutRepository extends JpaRepository<Burnout, Long> {

    Optional<Burnout> findByName(final String name);
}
