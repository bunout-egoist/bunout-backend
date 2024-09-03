package dough.member.domain.repository;

import dough.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialLoginId(String socialLoginId);

    Optional<Member> findByEmail(String email);

    boolean existsBySocialLoginId(String socialLoginId);

    @Query("""
            SELECT m
            FROM Member m
            JOIN FETCH m.burnout
            JOIN FETCH m.quest
            JOIN FETCH m.level
            JOIN FETCH m.notifications
            WHERE m.id = :memberId
            """)
    Optional<Member> findMemberById(@Param("memberId") final Long memberId);
}
