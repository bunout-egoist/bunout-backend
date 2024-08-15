package dough.login.domain.repository;


import dough.login.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.nio.channels.FileChannel;
import java.sql.Ref;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findBySocialLoginId(String socialLoginId);
}