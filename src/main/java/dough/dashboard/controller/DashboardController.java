package dough.dashboard.controller;

import dough.dashboard.dto.response.DashboardResponse;
import dough.dashboard.service.DashboardService;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import dough.quest.dto.response.TotalCompletedQuestsResponse;
import dough.quest.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    public final QuestService questService;
    public final DashboardService dashboardService;

    @GetMapping("/quests/{memberId}/{searchDate}")
    public ResponseEntity<List<CompletedQuestDetailResponse>> getCompletedQuestsDetail(
            @PathVariable("memberId") final Long memberId,
            @PathVariable("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date
    ) {
        final List<CompletedQuestDetailResponse> detailResponse = questService.getCompletedQuestsDetail(memberId, date);
        return ResponseEntity.ok().body(detailResponse);
    }

    @GetMapping("/total/{memberId}")
    public ResponseEntity<TotalCompletedQuestsResponse> getTotalCompletedQuests(@PathVariable("memberId") final Long memberId) {
        final TotalCompletedQuestsResponse totalCompletedQuestsResponse = dashboardService.getTotalCompletedQuests(memberId);
        return ResponseEntity.ok().body(totalCompletedQuestsResponse);
    }

    @GetMapping("/{memberId}/{year}/{month}")
    public ResponseEntity<DashboardResponse> getMonthlyDashboard(
            @PathVariable("memberId") final Long memberId,
            @PathVariable("year") final Long year,
            @PathVariable("month") final Long month
    ) {
        final DashboardResponse dashboardResponse = dashboardService.getMonthlyDashboard(memberId, year, month);
        return ResponseEntity.ok().body(dashboardResponse);
    }
}
