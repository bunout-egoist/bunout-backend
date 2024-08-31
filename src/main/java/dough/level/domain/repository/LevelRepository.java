package dough.level.domain.repository;

import dough.level.domain.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

    @Query("""
            SELECT level
            FROM Level level
            WHERE level.level = :level AND level.level = :level + 1
           """)
    List<Level> findCurrentAndNextLevel(@Param("level") final Integer level);

    Optional<Level> findByLevel(final Integer level);
}
