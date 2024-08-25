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

    @GetMapping("/quests/{memberId}/{searchDate}")
    public ResponseEntity<List<WeeklySummaryResponse>> getWeeklySummary(
            @PathVariable("memberId") final Long memberId,
            @PathVariable("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date
    ) {
        final List<WeeklySummaryResponse> detailResponse = questService.getWeeklySummary(memberId, date);
        return ResponseEntity.ok().body(detailResponse);
    }

    @GetMapping("/total/{memberId}")
    public ResponseEntity<CompletedQuestsTotalResponse> getCompletedQuestsTotal(@PathVariable("memberId") final Long memberId) {
        final CompletedQuestsTotalResponse completedQuestsTotalResponse = dashboardService.getCompletedQuestsTotal(memberId);
        return ResponseEntity.ok().body(completedQuestsTotalResponse);
    }

    @GetMapping("/{memberId}/{yearMonth}")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @PathVariable("memberId") final Long memberId,
            @PathVariable("yearMonth") final YearMonth yearMonth
    ) {
        final MonthlySummaryResponse monthlySummaryResponse = dashboardService.getMonthlySummary(memberId, yearMonth);
        return ResponseEntity.ok().body(monthlySummaryResponse);
    }
}
