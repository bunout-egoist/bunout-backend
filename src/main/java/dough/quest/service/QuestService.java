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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        final List<SelectedQuest> todayQuests = getTodayQuests(member, currentDate);

        if (todayQuests.isEmpty()) {
            createTodayQuests(member, currentDate)
                    .forEach(todayQuest -> todayQuests.add(todayQuest));
        }

        return todayQuests.stream()
                .map(todayQuest -> TodayQuestResponse.of(todayQuest.getQuest()))
                .toList();
    }

    private List<SelectedQuest> createTodayQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> todayQuests = updateTodayDailyQuests(member, currentDate);

        if (isSpecialQuestDay(currentDate)) {
            final Quest specialQuest = getTodaySpecialQuest(member.getBurnout());
            todayQuests.add(new SelectedQuest(member, specialQuest));
        }
        todayQuests.add(new SelectedQuest(member, member.getQuest()));

        return selectedQuestRepository.saveAll(todayQuests);
    }

    private List<SelectedQuest> getTodayQuests(final Member member, final LocalDate currentDate) {
        return selectedQuestRepository.findTodayDailyQuests(member.getId(), currentDate);
    }

    private Quest getTodaySpecialQuest(final Burnout burnout) {
        final List<Quest> specialQuests = questRepository.findSpecialQuestByBurnoutId(burnout.getId());
        Collections.shuffle(specialQuests);
        return specialQuests.get(0);
    }

    private Boolean isSpecialQuestDay(final LocalDate currentDate) {
        final DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        return dayOfWeek.equals(MONDAY) ||
                dayOfWeek.equals(THURSDAY) ||
                dayOfWeek.equals(SUNDAY);
    }

    private List<SelectedQuest> updateTodayDailyQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> incompleteDailyQuests = getIncompleteDailyQuests(member, currentDate);

        int neededCount = 2 - incompleteDailyQuests.size();

        if (neededCount > 0) {
            questRepository.findTodayDailyQuestsByMemberId(member.getId(), member.getLevel(), member.getBurnout().getId())
                    .stream()
                    .limit(neededCount)
                    .collect(Collectors.toList())
                    .forEach(todayDailyQuest -> incompleteDailyQuests.add(new SelectedQuest(member, todayDailyQuest)));
        }
        return incompleteDailyQuests;
    }

    private List<SelectedQuest> getIncompleteDailyQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> incompleteDailyQuests = selectedQuestRepository.findIncompleteDailyQuestsByMemberIdAndDate(member.getId(), currentDate.minusDays(1));
        return incompleteDailyQuests.stream()
                .map(incompleteDailyQuest -> {
                    incompleteDailyQuest.updateDueDate(currentDate);
                    return incompleteDailyQuest;
                }).collect(Collectors.toList());
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
        // TODO 수정 필요
        final QuestType questType = QuestType.getMappedQuestType(questRequest.getQuestType());
        final Quest newQuest = new Quest(
                questRequest.getDescription(),
                questRequest.getActivity(),
                questType,
                questRequest.getDifficulty(),
                new Burnout(1L, "호빵"),
                null
        );

        final Quest quest = questRepository.save(newQuest);
        return QuestResponse.of(quest);
    }

    public void update(final Long questId, final QuestUpdateRequest questUpdateRequest) {
        if (!questRepository.existsById(questId)) {
            throw new BadRequestException(NOT_FOUND_QUEST_ID);
        }

        // TODO 수정 필요
        final QuestType questType = QuestType.getMappedQuestType(questUpdateRequest.getQuestType());
        final Quest updateQuest = new Quest(
                questId,
                questUpdateRequest.getDescription(),
                questUpdateRequest.getActivity(),
                questType,
                questUpdateRequest.getDifficulty(),
                new Burnout(1L, "호빵"),
                null
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
