package dough.quest.controller;

import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.QuestResponse;
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

    @PostMapping
    public ResponseEntity<QuestResponse> createQuest(@RequestBody @Valid final QuestRequest questRequest) {
        final QuestResponse questResponse = questService.save(questRequest);
        return ResponseEntity.ok().body(questResponse);
    }

    @PutMapping("/{questId}")
    public ResponseEntity<Void> updateQuest(
            @PathVariable("questId") final Long questId,
            @RequestBody @Valid final QuestUpdateRequest questUpdateRequest) {
        questService.update(questId, questUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{questId}")
    public ResponseEntity<Void> deleteQuest(@PathVariable("questId") final Long questId) {
        questService.delete(questId);
        return ResponseEntity.ok().build();
    }
}
