package dough.quest.controller;

import dough.login.domain.Auth;
import dough.login.domain.Accessor;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.FixedQuestListResponse;
import dough.quest.dto.response.TodayQuestListResponse;
import dough.quest.service.QuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quests")
public class QuestController {

    private final QuestService questService;

    @GetMapping("/fixed")
    public ResponseEntity<FixedQuestListResponse> getFixedQuests(@Auth final Accessor accessor) {
        final FixedQuestListResponse fixedQuestListResponse = questService.getFixedQuests(accessor.getMemberId());
        return ResponseEntity.ok().body(fixedQuestListResponse);
    }

    @GetMapping("/fixed/{burnoutId}")
    public ResponseEntity<FixedQuestListResponse> getFixedQuests(
            @Auth final Accessor accessor,
            @PathVariable final Long burnoutId
    ) {
        final FixedQuestListResponse fixedQuestListResponse = questService.getFixedQuestsByBurnoutId(accessor.getMemberId(), burnoutId);
        return ResponseEntity.ok().body(fixedQuestListResponse);
    }

    @PostMapping("/today")
    public ResponseEntity<TodayQuestListResponse> getTodayQuests(@Auth final Accessor accessor) {
        final TodayQuestListResponse todayQuestListResponse = questService.updateTodayQuests(accessor.getMemberId());
        return ResponseEntity.ok().body(todayQuestListResponse);
    }

    @PostMapping
    public ResponseEntity<Void> createQuest(@RequestBody @Valid final QuestRequest questRequest) {
        questService.save(questRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{questId}")
    public ResponseEntity<Void> updateQuest(
            @PathVariable("questId") final Long questId,
            @RequestBody @Valid final QuestUpdateRequest questUpdateRequest) {
        questService.update(questId, questUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{questId}")
    public ResponseEntity<Void> deleteQuest(@PathVariable("questId") final Long questId) {
        questService.delete(questId);
        return ResponseEntity.noContent().build();
    }
}
