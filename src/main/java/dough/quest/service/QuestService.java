package dough.quest.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.global.exception.BadRequestException;
import dough.keyword.KeywordCode;
import dough.keyword.domain.Keyword;
import dough.keyword.domain.repository.KeywordRepository;
import dough.keyword.domain.type.ParticipationType;
import dough.keyword.domain.type.PlaceType;
import dough.login.service.TokenService;
import dough.member.domain.Member;
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
import dough.quest.dto.response.FixedQuestListResponse;
import dough.quest.dto.response.TodayQuestListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dough.global.exception.ExceptionCode.*;
import static dough.quest.domain.type.QuestType.BY_TYPE;
import static dough.quest.domain.type.QuestType.SPECIAL;
import static java.time.DayOfWeek.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final BurnoutRepository burnoutRepository;
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final TokenService tokenService;

    public TodayQuestListResponse updateTodayQuests() {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final LocalDate currentDate = LocalDate.now();
        final List<SelectedQuest> todayQuests = getTodayQuests(member, currentDate);

        if (todayQuests.isEmpty()) {
            createTodayQuests(member, currentDate)
                    .forEach(todayQuest -> todayQuests.add(todayQuest));
        }

        final KeywordCode keywordCode = getKeywords(todayQuests);

        return TodayQuestListResponse.of(keywordCode, todayQuests);
    }

    private List<SelectedQuest> createTodayQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> todayQuests = updateTodayByTypeQuests(member, currentDate);

        if (isSpecialQuestDay(currentDate)) {
            final Quest specialQuest = getTodaySpecialQuest(member.getBurnout());
            todayQuests.add(new SelectedQuest(member, specialQuest));
        }
        todayQuests.add(new SelectedQuest(member, member.getQuest()));

        return selectedQuestRepository.saveAll(todayQuests);
    }

    private KeywordCode getKeywords(final List<SelectedQuest> todayQuests) {
        final List<Keyword> keywords = todayQuests.stream()
                .filter(selectedQuest -> selectedQuest.getQuest().getQuestType().equals(BY_TYPE))
                .map(selectedQuest -> selectedQuest.getQuest().getKeyword())
                .collect(Collectors.toList());

        final String participationCode = ParticipationType.getParticipationCode(keywords);
        final String placeCode = PlaceType.getPlaceCode(keywords);

        return new KeywordCode(placeCode, participationCode);
    }

    private List<SelectedQuest> getTodayQuests(final Member member, final LocalDate currentDate) {
        return selectedQuestRepository.findTodayByTypeQuests(member.getId(), currentDate);
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

    private List<SelectedQuest> updateTodayByTypeQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> incompleteByTypeQuests = getIncompleteByTypeQuests(member, currentDate);

        int neededCount = 2 - incompleteByTypeQuests.size();

        if (neededCount > 0) {
            // TODO keyword가 같은 퀘스트 위주로 반환
            questRepository.findTodayByTypeQuestsByMemberId(member.getId(), member.getLevel().getLevel(), member.getBurnout().getId())
                    .stream()
                    .limit(neededCount)
                    .collect(Collectors.toList())
                    .forEach(todayByTypeQuest -> incompleteByTypeQuests.add(new SelectedQuest(member, todayByTypeQuest)));
        }
        return incompleteByTypeQuests;
    }

    private List<SelectedQuest> getIncompleteByTypeQuests(final Member member, final LocalDate currentDate) {
        final List<SelectedQuest> incompleteByTypeQuests = selectedQuestRepository.findIncompleteByTypeQuestsByMemberId(member.getId());
        return incompleteByTypeQuests.stream()
                .map(incompleteByTypeQuest -> {
                    incompleteByTypeQuest.updateDueDate(currentDate);
                    return incompleteByTypeQuest;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FixedQuestListResponse getFixedQuests(final Long burnoutId) {
        final Burnout burnout = burnoutRepository.findById(burnoutId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));

        final List<Quest> fixedQuests = questRepository.findFixedQuestsByBurnoutId(burnoutId);
        return FixedQuestListResponse.of(burnout, fixedQuests);
    }

    @Transactional(readOnly = true)
    public List<WeeklySummaryResponse> getWeeklySummary(final LocalDate date) {
        final Long memberId = tokenService.getMemberId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final LocalDate startDate = date.minusDays(3);
        final LocalDate endDate = date.plusDays(3);
        final CompletedQuestElements completedQuestElements = new CompletedQuestElements(selectedQuestRepository.findCompletedQuestsByMemberIdAndDate(member.getId(), startDate, endDate));
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

    public void save(final QuestRequest questRequest) {
        final Keyword keyword = keywordRepository.findByIsGroupAndIsOutside(questRequest.getIsGroup(), questRequest.getIsOutside())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_KEYWORD_ID));

        final Burnout burnout = burnoutRepository.findByName(questRequest.getBurnoutName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));

        final QuestType questType = QuestType.getMappedQuestType(questRequest.getQuestType());
        final Quest newQuest = new Quest(
                questRequest.getContent(),
                questType,
                questRequest.getDifficulty(),
                burnout,
                keyword
        );

        questRepository.save(newQuest);
    }

    public void update(final Long questId, final QuestUpdateRequest questUpdateRequest) {
        if (!questRepository.existsById(questId)) {
            throw new BadRequestException(NOT_FOUND_QUEST_ID);
        }

        final Keyword keyword = keywordRepository.findByIsGroupAndIsOutside(questUpdateRequest.getIsGroup(), questUpdateRequest.getIsOutside())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_KEYWORD_ID));

        final Burnout burnout = burnoutRepository.findByName(questUpdateRequest.getBurnoutName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));

        final QuestType questType = QuestType.getMappedQuestType(questUpdateRequest.getQuestType());
        final Quest updateQuest = new Quest(
                questId,
                questUpdateRequest.getContent(),
                questType,
                questUpdateRequest.getDifficulty(),
                burnout,
                keyword
        );

        questRepository.save(updateQuest);
    }

    public void delete(final Long questId) {
        final Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));

        checkQuestInUse(quest);

        questRepository.deleteByQuestId(questId);
    }

    private void checkQuestInUse(final Quest quest) {
        if (selectedQuestRepository.existsByQuest(quest)) {
            throw new BadRequestException(ALREADY_USED_QUEST_ID);
        }
    }
}
