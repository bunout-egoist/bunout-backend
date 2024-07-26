package dough.quest.service;

import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.request.QuestRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;

    public Long save(final QuestRequest questRequest) {
        final QuestType questType = QuestType.getMappedQuestType(questRequest.getQuestType());
        final Quest quest = new Quest(
                questRequest.getDescription(),
                questRequest.getActivity(),
                questType,
                questRequest.getDifficulty()
        );

        return questRepository.save(quest).getId();
    }
}
