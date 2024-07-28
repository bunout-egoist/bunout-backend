package dough.quest.service;

import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.domain.type.QuestType;
import dough.quest.dto.CompletedQuestFeedbackElement;
import dough.quest.dto.request.QuestRequest;
import dough.quest.dto.response.CompletedQuestDetailResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final QuestRepository questRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final MemberRepository memberRepository;

    public List<CompletedQuestDetailResponse> getCompletedQuestDetail(final Long memberId, final LocalDate date) {
        if (!memberRepository.existsById(memberId)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        List<CompletedQuestFeedbackElement> elements = selectedQuestRepository.findCompletedQuestFeedbackByMemberIdAndDate(memberId, date);
        return elements.stream()
                .map(element -> CompletedQuestDetailResponse.of(
                        element.getQuest(),
                        element.getFeedback()
                )).toList();
    }

    public Long save(final QuestRequest questRequest) {
        final QuestType questType = QuestType.getMappedQuestType(questRequest.getQuestType());
        final Quest quest = new Quest(
                questRequest.getDescription(),
                questRequest.getActivity(),
                questType,
                questRequest.getDifficulty()
        );

        return questRepository.save(quest).getId();
    }
}
