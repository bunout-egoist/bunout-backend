package dough.quest.service;

import dough.feedback.domain.Feedback;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import dough.quest.dto.response.QuestResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static dough.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final MemberRepository memberRepository;

    public List<CompletedQuestDetailResponse> getCompletedQuestsDetail(final Long memberId, final LocalDate date) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        List<SelectedQuest> selectedQuests = selectedQuestRepository.findCompletedQuestsByMemberIdAndDate(memberId, date);
        return selectedQuests.stream()
                .map(selectedQuest -> CompletedQuestDetailResponse.of(
                        selectedQuest.getQuest(),
                        selectedQuest.getFeedback()
                )).toList();
    }

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

    public void delete(Long questId) {
        if (!questRepository.existsById(questId)) {
            throw new BadRequestException(NOT_FOUND_QUEST_ID);
        }

        checkQuestInUse(questId);

        questRepository.deleteByQuestId(questId);
    }

    private void checkQuestInUse(final Long questId) {
        if (selectedQuestRepository.existsByQuestId(questId)) {
            throw new BadRequestException(ALREADY_USED_QUEST_ID);
        }
    }

//    public void completeSelectedQuestWithFeedback(Long selectedQuestId, Feedback feedback) {
//        selectedQuestRepository.updateFeedbackAndStatus(selectedQuestId, feedback);
//    }

    public void completeSelectedQuestWithFeedback(final SelectedQuest selectedQuest, final Feedback feedback) {
        selectedQuest.AddFeedbackToSelectedQuest(feedback);
        selectedQuestRepository.save(selectedQuest);
    }
}
