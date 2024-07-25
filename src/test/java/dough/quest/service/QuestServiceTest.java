package dough.quest.service;

import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.dto.request.QuestRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dough.quest.fixture.QuestFixture.DAILY_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
                "데일리퀘스트",
                3
        );

        given(questRepository.save(any(Quest.class))).willReturn(DAILY_QUEST1);

        // when
        final Long actualId = questService.save(questRequest);

        // then
        assertThat(actualId).isEqualTo(DAILY_QUEST1.getId());
    }
}
