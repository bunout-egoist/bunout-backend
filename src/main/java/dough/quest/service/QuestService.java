package dough.quest.service;

import dough.global.exception.BadRequestException;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.QuestResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dough.global.exception.ExceptionCode.NOT_FOUND_QUEST_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;

    public QuestResponse save(final QuestRequest questRequest) {
        final QuestType questType = QuestType.getMappedQuestType(questRequest.getQuestType());
        final Quest newQuest = new Quest(
                questRequest.getDescription(),
                questRequest.getActivity(),
                questType,
                questRequest.getDifficulty()
        );

        final Quest quest = questRepository.save(newQuest);
        return QuestResponse.of(quest);
    }

    public void update(final Long questId, final QuestUpdateRequest questUpdateRequest) {
        if (!questRepository.existsById(questId)) {
            throw new BadRequestException(NOT_FOUND_QUEST_ID);
        }

        final QuestType questType = QuestType.getMappedQuestType(questUpdateRequest.getQuestType());
        final Quest updateQuest = new Quest(
                questId,
                questUpdateRequest.getDescription(),
                questUpdateRequest.getActivity(),
                questType,
                questUpdateRequest.getDifficulty()
        );

        questRepository.save(updateQuest);
    }
}
