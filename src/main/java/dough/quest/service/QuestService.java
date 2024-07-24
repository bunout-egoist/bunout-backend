package dough.quest.service;

import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.dto.CompletedQuestFeedbackElement;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final SelectedQuestRepository selectedQuestRepository;

    public List<CompletedQuestDetailResponse> getCompletedQuestDetail(final Long memberId, final LocalDate searchDate) {
        List<CompletedQuestFeedbackElement> completedQuestFeedbackElements = selectedQuestRepository.findCompletedQuestFeedbackByMemberIdAndSearchDate(memberId, searchDate);
        return completedQuestFeedbackElements.stream()
                .map(completedQuest -> CompletedQuestDetailResponse.of(
                        completedQuest.getFeedback(),
                        completedQuest.getQuest()
                )).toList();
    }
}
