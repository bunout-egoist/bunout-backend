package dough.dashboard.controller;

import dough.dashboard.dto.response.TotalCompletedQuestCountResponse;
import dough.dashboard.service.DashboardService;
import dough.global.TestService;
import dough.quest.dto.response.CompletedQuestDetailResponse;
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
    public final TestService testService;

    @GetMapping("/quests/{memberId}/{searchDate}")
    public ResponseEntity<List<CompletedQuestDetailResponse>> getCompletedQuestDetail(
            @PathVariable("memberId") final Long memberId,
            @PathVariable("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date
    ) {
        final List<CompletedQuestDetailResponse> detailResponse = questService.getCompletedQuestDetail(memberId, date);
        return ResponseEntity.ok().body(detailResponse);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<TotalCompletedQuestCountResponse> getTotalCompletedQuestCount(@PathVariable("memberId") final Long memberId) {
        final TotalCompletedQuestCountResponse totalCompletedQuestCountResponse = dashboardService.getTotalCompletedQuestCount(memberId);
        return ResponseEntity.ok().body(totalCompletedQuestCountResponse);
    }
}
