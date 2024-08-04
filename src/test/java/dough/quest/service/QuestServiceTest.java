package dough.quest.service;

import dough.global.exception.BadRequestException;
import dough.global.exception.InvalidDomainException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import dough.quest.dto.response.QuestResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static dough.feedback.fixture.CompletedQuestDetailFixture.COMPLETED_QUEST_DETAILS;
import static dough.global.exception.ExceptionCode.*;
import static dough.member.fixture.MemberFixture.MEMBER;
import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.COMPLETED_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.COMPLETED_QUEST2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @DisplayName("퀘스트를 추가할 수 있다.")
    @Test
    void save() {
        // given
        final QuestRequest questRequest = new QuestRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기",
                "데일리",
                3
        );

        given(questRepository.save(any(Quest.class)))
                .willReturn(DAILY_QUEST1);

        // when
        final QuestResponse questResponse = questService.save(questRequest);

        // then
        assertThat(questResponse).usingRecursiveComparison()
                .isEqualTo(QuestResponse.of(DAILY_QUEST1));
    }

    @DisplayName("퀘스트 타입이 맞지 않을 경우 예외가 발생한다.")
    @Test
    void save_QuestTypeInvalid() {
        // given
        final QuestRequest questRequest = new QuestRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기",
                "퀘스트 타입 오류",
                3
        );

        // when & then
        assertThatThrownBy(() -> questService.save(questRequest))
                .isInstanceOf(InvalidDomainException.class)
                .extracting("code")
                .isEqualTo(INVALID_QUEST_TYPE.getCode());
    }

    @DisplayName("달성한 퀘스트의 상세 정보를 조회할 수 있다.")
    @Test
    void getCompletedQuestDetail() {
        // given
        final List<SelectedQuest> selectedQuests = List.of(COMPLETED_QUEST1, COMPLETED_QUEST2);

        given(memberRepository.existsById(any()))
                .willReturn(true);
        given(selectedQuestRepository.findConpletedQuestByMemberIdAndDate(anyLong(), any()))
                .willReturn(selectedQuests);

        List<CompletedQuestDetailResponse> actualResponse = questService.getCompletedQuestDetail(MEMBER.getId(), LocalDate.now());

        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(COMPLETED_QUEST_DETAILS.stream()
                        .map(completedQuestDetail ->
                                CompletedQuestDetailResponse.of(
                                        completedQuestDetail.quest,
                                        completedQuestDetail.feedback
                                ))
                        .toList());
    }

    @DisplayName("멤버 아이디가 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void getCompletedQuestDetail_NotFoundMemberId() {
        // given
        Long id = 1L;

        // given & when & then
        assertThatThrownBy(() -> questService.getCompletedQuestDetail(id, any()))
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
                4
        );

        given(questRepository.existsById(any()))
                .willReturn(true);
        given(questRepository.save(any()))
                .willReturn(DAILY_QUEST1);

        // when
        questService.update(DAILY_QUEST1.getId(), questUpdateRequest);

        // then
        verify(questRepository).existsById(any());
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
                4
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
        given(questRepository.existsById(any()))
                .willReturn(true);
        given(selectedQuestRepository.existsByQuestId(any()))
                .willReturn(false);

        // when
        questService.delete(DAILY_QUEST1.getId());

        // then
        verify(questRepository).deleteByQuestId(any());
        verify(selectedQuestRepository).existsByQuestId(any());
    }

    @DisplayName("존재하지 않는 questId를 삭제할 시 예외가 발생한다.")
    @Test
    void delete_NotFoundQuestId() {
        // given
        given(questRepository.existsById(any()))
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
        given(questRepository.existsById(any()))
                .willReturn(true);
        given(selectedQuestRepository.existsByQuestId(any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> questService.delete(any()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(ALREADY_USED_QUEST_ID.getCode());
    }
}
