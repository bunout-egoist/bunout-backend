package dough.dashboard.domain.repository;

import dough.dashboard.domain.Dashboard;
import dough.dashboard.dto.CompletedQuestCountElement;
import dough.global.annotation.TimeTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    @TimeTrace
    @Query("""
            SELECT new dough.dashboard.dto.CompletedQuestCountElement(d.dailyCount, d.fixedCount, d.specialCount)
            FROM Dashboard d
            WHERE d.member.id = :memberId
            """)
    List<CompletedQuestCountElement> getDashboardByMemberIdAndDate(@Param("memberId") Long memberId);
}
