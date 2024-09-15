package dough.dashboard.controller;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.dashboard.service.DashboardService;
import dough.login.domain.Accessor;
import dough.login.domain.Auth;
import dough.quest.dto.response.TotalAndStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    public final DashboardService dashboardService;

    @GetMapping("/weekly/{searchDate}")
    public ResponseEntity<List<WeeklySummaryResponse>> getWeeklySummary(
            @Auth final Accessor accessor,
            @PathVariable("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date
    ) {
        final List<WeeklySummaryResponse> detailResponse = dashboardService.getWeeklySummary(accessor.getMemberId(), date);
        return ResponseEntity.ok().body(detailResponse);
    }

    @GetMapping("/total")
    public ResponseEntity<TotalAndStatisticsResponse> getCompletedQuestsTotal(@Auth final Accessor accessor) {
        final TotalAndStatisticsResponse TotalAndStatisticsResponse = dashboardService.getCompletedQuestsTotalAndStatistics(accessor.getMemberId());
        return ResponseEntity.ok().body(TotalAndStatisticsResponse);
    }

    @GetMapping("/monthly/{yearMonth}")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @Auth final Accessor accessor,
            @PathVariable("yearMonth") final YearMonth yearMonth
    ) {
        final MonthlySummaryResponse monthlySummaryResponse = dashboardService.getMonthlySummary(accessor.getMemberId(), yearMonth);
        return ResponseEntity.ok().body(monthlySummaryResponse);
    }
}
