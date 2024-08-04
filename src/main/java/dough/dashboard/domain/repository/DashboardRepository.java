package dough.dashboard.domain.repository;

import dough.dashboard.domain.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {


    // TODO 날짜 비교
    @Query("""
            SELECT d
            FROM Dashboard d
            WHERE d.member.id = :memberId
            """)
    List<Dashboard> getDashboardByMemberIdAndDate(@Param("memberId") Long memberId, @Param("year") Long year, @Param("month") Long month);
}
