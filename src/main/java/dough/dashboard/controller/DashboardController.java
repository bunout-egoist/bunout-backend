package dough.dashboard.controller;

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

    @GetMapping("/quests/{memberId}/{searchDate}")
    public ResponseEntity<List<CompletedQuestDetailResponse>> getCompletedQuestDetail(
            @PathVariable("memberId") final Long memberId,
            @PathVariable("searchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date
    ) {
        final List<CompletedQuestDetailResponse> detailResponse = questService.getCompletedQuestDetail(memberId, date);
        return ResponseEntity.ok().body(detailResponse);
    }


}
