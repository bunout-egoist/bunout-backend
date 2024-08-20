package dough.level.domain.repository;

import dough.level.domain.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

    Optional<Level> findByLevel(final Integer level);
}
