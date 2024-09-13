package dough.member.domain.repository;

import dough.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByRefreshToken(final String refreshToken);

    Optional<Member> findBySocialLoginId(final String socialLoginId);

    @Query("""
            SELECT m
            FROM Member m
            LEFT JOIN FETCH m.burnout
            LEFT JOIN FETCH m.quest
            LEFT JOIN FETCH m.level
            WHERE m.id = :memberId
            """)
    Optional<Member> findMemberById(@Param("memberId") final Long memberId);
}
