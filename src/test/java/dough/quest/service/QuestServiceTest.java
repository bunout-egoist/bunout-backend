package dough.quest.service;

import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.global.exception.InvalidDomainException;
import dough.keyword.KeywordCode;
import dough.keyword.domain.repository.KeywordRepository;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
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

import java.util.List;
import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.SOBORO;
import static dough.global.exception.ExceptionCode.*;
import static dough.keyword.domain.type.ParticipationType.ALONE;
import static dough.keyword.domain.type.PlaceType.ANYWHERE;
import static dough.keyword.fixture.KeywordFixture.OUTSIDE_ALONE;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.QuestFixture.*;
import static dough.quest.fixture.SelectedQuestFixture.*;
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
                "15분 운동하기",
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "유형별퀘스트",
                true,
                false,
                "소보로"
        );

        given(keywordRepository.findByIsGroupAndIsOutside(anyBoolean(), anyBoolean()))
                .willReturn(Optional.of(OUTSIDE_ALONE));
        given(burnoutRepository.findByName(anyString()))
                .willReturn(Optional.of(SOBORO));
        given(questRepository.save(any(Quest.class)))
                .willReturn(BY_TYPE_QUEST1);

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
                "15분 운동하기",
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "퀘스트 타입 오류",
                true,
                false,
                "소보로"
        );

        given(keywordRepository.findByIsGroupAndIsOutside(anyBoolean(), anyBoolean()))
                .willReturn(Optional.of(OUTSIDE_ALONE));
        given(burnoutRepository.findByName(anyString()))
                .willReturn(Optional.of(SOBORO));

        // when & then
        assertThatThrownBy(() -> questService.save(questRequest))
                .isInstanceOf(InvalidDomainException.class)
                .extracting("code")
                .isEqualTo(INVALID_QUEST_TYPE.getCode());
    }

    @DisplayName("퀘스트를 업데이트 할 수 있다.")
    @Test
    void update() {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "20분 운동하기",
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "스페셜퀘스트",
                true,
                false,
                "소보로"
        );

        given(questRepository.existsById(anyLong()))
                .willReturn(true);
        given(keywordRepository.findByIsGroupAndIsOutside(anyBoolean(), anyBoolean()))
                .willReturn(Optional.of(OUTSIDE_ALONE));
        given(burnoutRepository.findByName(anyString()))
                .willReturn(Optional.of(SOBORO));
        given(questRepository.save(any()))
                .willReturn(BY_TYPE_QUEST1);

        // when
        questService.update(BY_TYPE_QUEST1.getId(), questUpdateRequest);

        // then
        verify(questRepository).existsById(anyLong());
        verify(keywordRepository).findByIsGroupAndIsOutside(anyBoolean(), anyBoolean());
        verify(burnoutRepository).findByName(anyString());
        verify(questRepository).save(any());
    }

    @DisplayName("존재하지 않는 questId를 입력받으면 예외가 발생한다.")
    @Test
    void update_NotFoundQuestId() {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "20분 운동하기",
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "스페셜퀘스트",
                true,
                false,
                "소보로"
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
                .willReturn(Optional.of(BY_TYPE_QUEST1));
        given(selectedQuestRepository.existsByQuest(any()))
                .willReturn(false);

        // when
        questService.delete(BY_TYPE_QUEST1.getId());

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
                .willReturn(Optional.of(BY_TYPE_QUEST1));
        given(selectedQuestRepository.existsByQuest(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> questService.delete(any()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_USED_QUEST_ID.getCode());
    }

    @DisplayName("멤버의 번아웃 유형에 해당하는 고정퀘스트를 조회할 수 있다.")
    @Test
    void getFixedQuests() {
        // given
        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(questRepository.findFixedQuestsByBurnoutId(anyLong()))
                .willReturn(List.of(FIXED_QUEST1, FIXED_QUEST2));

        // when
        final FixedQuestListResponse actualResponses = questService.getFixedQuests(GOEUN.getId());

        // then
        assertThat(actualResponses).usingRecursiveComparison()
                .isEqualTo(FixedQuestListResponse.of(GOEUN, SOBORO, List.of(FIXED_QUEST1, FIXED_QUEST2)));
    }

    @DisplayName("번아웃 유형에 해당하는 고정퀘스트를 조회할 수 있다.")
    @Test
    void getFixedQuestsByBurnoutId() {
        // given
        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(burnoutRepository.findById(anyLong()))
                .willReturn(Optional.of(SOBORO));
        given(questRepository.findFixedQuestsByBurnoutId(anyLong()))
                .willReturn(List.of(FIXED_QUEST1, FIXED_QUEST2));

        // when
        final FixedQuestListResponse actualResponses = questService.getFixedQuestsByBurnoutId(GOEUN.getId(), SOBORO.getId());

        // then
        assertThat(actualResponses).usingRecursiveComparison()
                .isEqualTo(FixedQuestListResponse.of(GOEUN, SOBORO, List.of(FIXED_QUEST1, FIXED_QUEST2)));
    }

    @DisplayName("오늘의 퀘스트를 받을 수 있다.")
    @Test
    void updateTodayQuests() {
        // given
        final List<SelectedQuest> todayQuests = List.of(IN_PROGRESS_QUEST1, IN_PROGRESS_QUEST2, IN_PROGRESS_QUEST3);

        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(selectedQuestRepository.findTodayQuests(anyLong(), any()))
                .willReturn(todayQuests);

        // when
        final TodayQuestListResponse actualResponse = questService.updateTodayQuests(GOEUN.getId());

        // then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(TodayQuestListResponse.of(SOBORO, GOEUN, new KeywordCode(ANYWHERE.getCode(), ALONE.getCode()), todayQuests));
    }
}
