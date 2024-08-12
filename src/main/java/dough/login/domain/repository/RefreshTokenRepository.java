package dough.login.domain.repository;


import dough.login.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}