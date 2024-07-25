package dough.quest.controller;

import dough.quest.dto.request.QuestRequest;
import dough.quest.service.QuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quests")
public class QuestController {

    private final QuestService questService;

    @PostMapping
    public ResponseEntity<Void> saveQuest(@RequestBody @Valid final QuestRequest questRequest) {
        questService.save(questRequest);
        return ResponseEntity.ok().build();
    }
}
