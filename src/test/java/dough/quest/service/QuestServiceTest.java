package dough.quest.service;

import dough.global.exception.InvalidDomainException;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.request.QuestUpdateRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dough.global.exception.ExceptionCode.INVALID_QUEST_TYPE;
import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
public class QuestServiceTest {

    @InjectMocks
    private QuestService questService;

    @Mock
    private QuestRepository questRepository;

    @DisplayName("퀘스트를 추가할 수 있다.")
    @Test
    void saveQuest() {
        // given
        final QuestRequest questRequest = new QuestRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기",
                "데일리",
                3
        );

        given(questRepository.save(any(Quest.class))).willReturn(DAILY_QUEST1);

        // when
        final Long actualId = questService.save(questRequest);

        // then
        assertThat(actualId).isEqualTo(DAILY_QUEST1.getId());
    }

    @DisplayName("퀘스트 타입이 맞지 않을 경우 예외가 발생한다.")
    @Test
    void saveQuest_QuestTypeInvalid() {
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

    @DisplayName("퀘스트를 업데이트 할 수 있다.")
    @Test
    void updateQuest() {
        // given
        final QuestUpdateRequest questUpdateRequest = new QuestUpdateRequest(
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "20분 운동하기",
                "스페셜",
                4
        );

        final QuestType questType = QuestType.getMappedQuestType(questUpdateRequest.getQuestType());

        final Quest updateQuest = new Quest(
                DAILY_QUEST1.getId(),
                "점심시간, 몸과 마음을 건강하게 유지하며",
                "20분 운동하기",
                questType,
                4
        );

        given(questRepository.existsById(DAILY_QUEST1.getId()))
                .willReturn(true);
        given(questRepository.save(any()))
                .willReturn(updateQuest);

        // when
        questService.update(DAILY_QUEST1.getId(), questUpdateRequest);

        // then
        verify(questRepository).existsById(any());
        verify(questRepository).save(any());
    }
}
