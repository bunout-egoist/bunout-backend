package dough.quest.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.feedback.domain.Feedback;
import dough.global.exception.BadRequestException;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import dough.quest.dto.response.FixedQuestResponse;
import dough.quest.dto.response.QuestResponse;
import dough.quest.dto.response.TodayQuestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static dough.global.exception.ExceptionCode.*;
import static java.time.DayOfWeek.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final BurnoutRepository burnoutRepository;
    private final MemberRepository memberRepository;

    public List<TodayQuestResponse> updateTodayQuests(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final LocalDate currentDate = LocalDate.now();
        List<SelectedQuest> todayQuests = getTodayDailyQuests(member, currentDate);

        if (todayQuests.isEmpty()) {
            todayQuests = updateTodayDailyQuests(member, currentDate);

            final Quest specialQuest = getTodaySpecialQuest(currentDate, member.getBurnout());
            todayQuests.add(new SelectedQuest(member, specialQuest));
            todayQuests.add(new SelectedQuest(member, member.getQuest()));
        }

        final List<SelectedQuest> savedTodayQuests = selectedQuestRepository.saveAll(todayQuests);
        return savedTodayQuests.stream()
                .map(savedTodayQuest -> TodayQuestResponse.of(savedTodayQuest.getQuest()))
                .toList();
    }

    private List<SelectedQuest> getTodayDailyQuests(final Member member, final LocalDate currentDate) {
        return selectedQuestRepository.findTodayDailyQuests(member.getId(), currentDate);
    }

    private Quest getTodaySpecialQuest(final LocalDate currentDate, final Burnout burnout) {
        if (currentDate.getDayOfWeek().equals(MONDAY) ||
                currentDate.getDayOfWeek().equals(THURSDAY) ||
                currentDate.getDayOfWeek().equals(SUNDAY)
        ) {
            List<Quest> specialQuests = questRepository.findSpecialQuestByBurnoutId(burnout.getId());
            Collections.shuffle(specialQuests);
            return specialQuests.get(0);
        }
        return null;
    }

    private List<SelectedQuest> updateTodayDailyQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> selectedQuests = selectedQuestRepository.findIncompletedDailyQuestsByMemberIdAndDate(member.getId(), currentDate.minusDays(1));
        final List<SelectedQuest> updatedQuests = selectedQuests.stream()
                .map(selectedQuest -> {
                    selectedQuest.updateDueDate(currentDate);
                    return selectedQuest;
                })
                .toList();

        int neededCount = 2 - selectedQuests.size();

        if (neededCount > 0) {
            final List<Quest> quests = questRepository.findTodayDailyQuestsByMemberId(member.getId(), member.getLevel(), member.getBurnout().getId())
                    .stream()
                    .limit(neededCount)
                    .toList();
            quests.stream().map(quest -> updatedQuests.add(new SelectedQuest(member, quest)));
        }
        return updatedQuests;
    }

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
    public List<CompletedQuestDetailResponse> getCompletedQuestDetail(final Long memberId, final LocalDate date) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        List<SelectedQuest> selectedQuests = selectedQuestRepository.findCompletedQuestByMemberIdAndDate(memberId, date);
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
