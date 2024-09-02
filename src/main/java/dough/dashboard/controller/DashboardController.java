package dough.dashboard.controller;

import dough.dashboard.dto.response.MonthlySummaryResponse;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.dashboard.service.DashboardService;
import dough.quest.dto.response.CompletedQuestsTotalResponse;
import dough.quest.service.QuestService;
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

    public final QuestService questService;
    public final DashboardService dashboardService;

    @GetMapping("/weekly/{searchDate}")
    public ResponseEntity<List<WeeklySummaryResponse>> getWeeklySummary(@PathVariable("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date) {
        final List<WeeklySummaryResponse> detailResponse = questService.getWeeklySummary(date);
        return ResponseEntity.ok().body(detailResponse);
    }

    @GetMapping("/total")
    public ResponseEntity<CompletedQuestsTotalResponse> getCompletedQuestsTotal() {
        final CompletedQuestsTotalResponse completedQuestsTotalResponse = dashboardService.getCompletedQuestsTotal();
        return ResponseEntity.ok().body(completedQuestsTotalResponse);
    }

    @GetMapping("/monthly/{yearMonth}")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(@PathVariable("yearMonth") final YearMonth yearMonth) {
        final MonthlySummaryResponse monthlySummaryResponse = dashboardService.getMonthlySummary(yearMonth);
        return ResponseEntity.ok().body(monthlySummaryResponse);
    }
}