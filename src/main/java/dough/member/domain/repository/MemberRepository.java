package dough.member.domain.repository;

import dough.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialLoginId(String socialLoginId);

    

    Optional<Member> findById(Long id);

    boolean existsBySocialLoginId(String socialLoginId);
}
