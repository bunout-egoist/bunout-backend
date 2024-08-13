package dough.quest.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.feedback.domain.Feedback;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.QuestFeedback;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.CompletedQuestElements;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.FixedQuestResponse;
import dough.quest.dto.response.QuestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.*;
import static dough.quest.domain.type.QuestType.SPECIAL;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final BurnoutRepository burnoutRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<FixedQuestResponse> getFixedQuests(final Long burnoutId) {
        if (!burnoutRepository.existsById(burnoutId)) {
            throw new BadRequestException(NOT_FOUND_BURNOUT_ID);
        }

        final List<Quest> fixedQuests = questRepository.findFixedQuestsByBurnoutId(burnoutId);
        return fixedQuests.stream()
                .map(fixedQuest -> FixedQuestResponse.of(fixedQuest))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WeeklySummaryResponse> getWeeklySummary(final Long memberId, final LocalDate date) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        final LocalDate startDate = date.minusDays(3);
        final LocalDate endDate = date.plusDays(3);
        final CompletedQuestElements completedQuestElements = new CompletedQuestElements(selectedQuestRepository.findCompletedQuestsByMemberIdAndDate(memberId, startDate, endDate));
        final Map<LocalDate, List<QuestFeedback>> questFeedbackMap = completedQuestElements.toQuestFeedbackMap();

        return getWeeklySummaryResponses(questFeedbackMap);
    }

    private List<WeeklySummaryResponse> getWeeklySummaryResponses(Map<LocalDate, List<QuestFeedback>> questFeedbackMap) {
        return questFeedbackMap.entrySet()
                .stream()
                .map(map -> WeeklySummaryResponse.of(
                        map.getKey(),
                        map.getValue(),
                        map.getValue().stream()
                                .filter(questFeedback -> questFeedback.getQuest().getQuestType() != SPECIAL)
                                .count()
                ))
                .collect(Collectors.toList());
    }

    public QuestResponse save(final QuestRequest questRequest) {
        final QuestType questType = QuestType.getMappedQuestType(questRequest.getQuestType());
        final Quest newQuest = new Quest(
                questRequest.getDescription(),
                questRequest.getActivity(),
                questType,
                questRequest.getDifficulty(),
                new Burnout(1L, "호빵")
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
                questUpdateRequest.getDifficulty(),
                new Burnout(1L, "호빵")
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
