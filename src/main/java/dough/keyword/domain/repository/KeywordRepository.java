package dough.keyword.domain.repository;

import dough.keyword.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByIsGroupAndIsOutside(final Boolean isGroup, final Boolean isOutside);
}
