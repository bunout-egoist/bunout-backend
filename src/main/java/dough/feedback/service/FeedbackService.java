package dough.feedback.service;

import dough.feedback.domain.Feedback;
import dough.feedback.domain.repository.FeedbackRepository;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.MemberLevel;
import dough.level.domain.repository.LevelRepository;
import dough.level.service.LevelService;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.SelectedQuestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dough.global.exception.ExceptionCode.*;
import static dough.quest.domain.type.QuestType.SPECIAL;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final MemberRepository memberRepository;
    private final LevelService levelService;

    public FeedbackResponse createFeedback(final Long memberId, final FeedbackRequest feedbackRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final SelectedQuest selectedQuest = selectedQuestRepository.findById(feedbackRequest.getSelectedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_SELECTED_QUEST_ID));

        final Feedback feedback = new Feedback(
                member,
                selectedQuest,
                feedbackRequest.getImageUrl(),
                feedbackRequest.getDifficulty()
        );

        final Feedback savedFeedback = feedbackRepository.save(feedback);

        selectedQuest.updateFeedback(savedFeedback);
        selectedQuestRepository.save(selectedQuest);

        final Integer currentExp = member.getExp();

        if (selectedQuest.getQuest().getQuestType().equals(SPECIAL)) {
            member.updateExp(currentExp + SPECIAL.getExp());
        } else {
            member.updateExp(currentExp + 15);
        }

        final MemberLevel memberLevel = levelService.updateLevel(member);
        memberRepository.save(memberLevel.getMember());
        return FeedbackResponse.of(memberLevel);
    }
}
