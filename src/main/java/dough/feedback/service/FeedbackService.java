package dough.feedback.service;

import dough.feedback.domain.Feedback;
import dough.feedback.domain.repository.FeedbackRepository;
import dough.feedback.dto.request.FeedbackRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.SelectedQuestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final SelectedQuestRepository selectedQuestRepository;
    private final MemberRepository memberRepository;

    public Feedback save(Long questId, FeedbackRequest feedbackRequest) {
        // questId로 SelectedQuest 조회
        SelectedQuest selectedQuest = selectedQuestRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("quest ID: " + questId + " is not exist"));

        // Member 찾기
        Long memberId = selectedQuest.getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member ID: " + memberId + "is not exist"));

        /////////////////////////

        // Feedback 객체 생성
        final Feedback feedback = new Feedback(
                member,
                selectedQuest,
                feedbackRequest.getImageUrl(),
                feedbackRequest.getDifficulty()
        );

        // 저장
        final Feedback savedFeedback = feedbackRepository.save(feedback);

        return savedFeedback;
    }
}
