package dough.quest.service;

import dough.burnout.domain.repository.BurnoutRepository;
import dough.dashboard.dto.response.WeeklySummaryResponse;
import dough.global.exception.BadRequestException;
import dough.global.exception.InvalidDomainException;
import dough.keyword.KeywordCode;
import dough.keyword.domain.repository.KeywordRepository;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.QuestFeedback;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.FixedQuestListResponse;
import dough.quest.dto.response.TodayQuestListResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.ENTHUSIAST;
import static dough.global.exception.ExceptionCode.*;
import static dough.keyword.domain.type.ParticipationType.ALONE;
import static dough.keyword.domain.type.PlaceType.ANYWHERE;
import static dough.keyword.fixture.KeywordFixture.OUTSIDE_ALONE;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.CompletedQuestElementFixture.QUEST_ELEMENT1;
import static dough.quest.fixture.QuestFixture.*;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
public class QuestServiceTest {

    @InjectMocks
    private QuestService questService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @Mock
    private BurnoutRepository burnoutRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @DisplayName("퀘스트를 추가할 수 있다.")
    @Test
    void save() {
        // given
        final QuestRequest questRequest = new QuestRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기",
                "데일리",
                3,
                true,
                false,
                "열광형"
        );

        given(keywordRepository.findByIsGroupAndIsOutside(anyBoolean(), anyBoolean()))
                .willReturn(Optional.of(OUTSIDE_ALONE));
        given(burnoutRepository.findByName(anyString()))
                .willReturn(Optional.of(ENTHUSIAST));
        given(questRepository.save(any(Quest.class)))
                .willReturn(DAILY_QUEST1);

        // when
        questService.save(questRequest);

        // then
        verify(keywordRepository).findByIsGroupAndIsOutside(anyBoolean(), anyBoolean());
        verify(burnoutRepository).findByName(anyString());
        verify(questRepository).save(any());
    }

    @DisplayName("퀘스트 타입이 맞지 않을 경우 예외가 발생한다.")
    @Test
    void save_QuestTypeInvalid() {
        // given
        final QuestRequest questRequest = new QuestRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기",
                "퀘스트 타입 오류",
                3,
                true,
                false,
                "열광형"
        );

        given(keywordRepository.findByIsGroupAndIsOutside(anyBoolean(), anyBoolean()))
                .willReturn(Optional.of(OUTSIDE_ALONE));
        given(burnoutRepository.findByName(anyString()))
                .willReturn(Optional.of(ENTHUSIAST));

        // when & then
        assertThatThrownBy(() -> questService.save(questRequest))
                .isInstanceOf(InvalidDomainException.class)
                .extracting("code")
                .isEqualTo(INVALID_QUEST_TYPE.getCode());
    }

    @DisplayName("달성한 퀘스트의 상세 정보를 조회할 수 있다.")
    @Test
    void getWeeklySummary() {
        // given
        final Long memberId = 1L;

        final LocalDate date = LocalDate.now();
        final LocalDate startDate = date.minusDays(3);
        final LocalDate endDate = date.plusDays(3);

        given(memberRepository.existsById(any()))
                .willReturn(true);
        given(selectedQuestRepository.findCompletedQuestsByMemberIdAndDate(memberId, startDate, endDate))
                .willReturn(List.of(QUEST_ELEMENT1));

        // when
        final List<WeeklySummaryResponse> actualResponse = questService.getWeeklySummary(GOEUN.getId(), LocalDate.now());

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(List.of(WeeklySummaryResponse.of(LocalDate.of(2024, 8, 11), List.of(new QuestFeedback(DAILY_QUEST1, "https://~")), 1L)));
    }

    @DisplayName("멤버 아이디가 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void getWeeklySummary_NotFoundMemberId() {
        // given
        Long id = 1L;

        // given & when & then
        assertThatThrownBy(() -> questService.getWeeklySummary(id, any()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_MEMBER_ID.getCode());
    }

    @DisplayName("퀘스트를 업데이트 할 수 있다.")
    @Test
    void update() {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "20분 운동하기",
                "스페셜",
                4,
                true,
                false,
                "열광형"
        );

        given(questRepository.existsById(any()))
                .willReturn(true);
        given(keywordRepository.findByIsGroupAndIsOutside(anyBoolean(), anyBoolean()))
                .willReturn(Optional.of(OUTSIDE_ALONE));
        given(burnoutRepository.findByName(anyString()))
                .willReturn(Optional.of(ENTHUSIAST));
        given(questRepository.save(any()))
                .willReturn(DAILY_QUEST1);

        // when
        questService.update(DAILY_QUEST1.getId(), questUpdateRequest);

        // then
        verify(questRepository).existsById(any());
        verify(keywordRepository).findByIsGroupAndIsOutside(anyBoolean(), anyBoolean());
        verify(burnoutRepository).findByName(anyString());
        verify(questRepository).save(any());
    }

    @DisplayName("존재하지 않는 questId를 입력받으면 예외가 발생한다.")
    @Test
    void update_NotFoundQuestId() {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "20분 운동하기",
                "스페셜",
                4,
                true,
                false,
                "열광형"
        );

        given(questRepository.existsById(any()))
                .willThrow(new BadRequestException(NOT_FOUND_QUEST_ID));

        // when & then
        assertThatThrownBy(() -> questService.update(any(), questUpdateRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_QUEST_ID.getCode());
    }

    @DisplayName("퀘스트를 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        given(questRepository.findById(any()))
                .willReturn(Optional.of(DAILY_QUEST1));
        given(selectedQuestRepository.existsByQuest(any()))
                .willReturn(false);

        // when
        questService.delete(DAILY_QUEST1.getId());

        // then
        verify(questRepository).deleteByQuestId(any());
        verify(selectedQuestRepository).existsByQuest(any());
    }

    @DisplayName("존재하지 않는 questId를 삭제할 시 예외가 발생한다.")
    @Test
    void delete_NotFoundQuestId() {
        // given
        given(questRepository.findById(any()))
                .willThrow(new BadRequestException(NOT_FOUND_QUEST_ID));

        // when & then
        assertThatThrownBy(() -> questService.delete(any()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_QUEST_ID.getCode());
    }

    @DisplayName("questId를 이미 사용하고 있는 회원이 있을 시 예외가 발생한다.")
    @Test
    void delete_AlreadyUsedQuestId() {
        // given
        given(questRepository.findById(any()))
                .willReturn(Optional.of(DAILY_QUEST1));
        given(selectedQuestRepository.existsByQuest(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> questService.delete(any()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_USED_QUEST_ID.getCode());
    }

    @DisplayName("번아웃 유형에 해당하는 고정퀘스트를 조회할 수 있다.")
    @Test
    void getFixedQuests() {
        // given
        given(burnoutRepository.findById(any()))
                .willReturn(Optional.of(ENTHUSIAST));
        given(questRepository.findFixedQuestsByBurnoutId(anyLong()))
                .willReturn(List.of(FIXED_QUEST1, FIXED_QUEST2));

        // when
        final FixedQuestListResponse actualResponses = questService.getFixedQuests(ENTHUSIAST.getId());

        // then
        assertThat(actualResponses).usingRecursiveComparison()
                .isEqualTo(FixedQuestListResponse.of(ENTHUSIAST, List.of(FIXED_QUEST1, FIXED_QUEST2)));
    }

    @DisplayName("오늘의 퀘스트를 받을 수 있다.")
    @Test
    void updateTodayQuests() {
        // given
        final List<SelectedQuest> todayQuests = List.of(IN_PROGRESS_QUEST1, IN_PROGRESS_QUEST2);

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(selectedQuestRepository.findTodayDailyQuests(anyLong(), any()))
                .willReturn(todayQuests);

        // when
        final TodayQuestListResponse actualResponse = questService.updateTodayQuests(GOEUN.getId());

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(TodayQuestListResponse.of(new KeywordCode(ANYWHERE.getCode(), ALONE.getCode()), todayQuests));
    }
}
