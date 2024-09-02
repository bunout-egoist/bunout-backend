package dough.level.domain.repository;

import dough.level.domain.Level;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

    @Query("""
            SELECT level
            FROM Level level
            WHERE level.accumulatedExp >= :exp
            ORDER BY level.accumulatedExp ASC
            """)
    List<Level> findTopByExp(@Param("exp") final Integer exp, final Pageable pageable);

    Optional<Level> findByLevel(final Integer level);
}
