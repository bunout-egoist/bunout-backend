package dough.feedback.service;

import dough.feedback.domain.Feedback;
import dough.feedback.domain.repository.FeedbackRepository;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.login.domain.type.SocialLoginType;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.quest.domain.type.QuestStatus;
import dough.quest.domain.type.QuestType;
import dough.quest.service.QuestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.burnout.fixture.BurnoutFixture.ENTHUSIAST;
import static dough.keyword.fixture.KeywordFixture.INSIDE_ALONE;
import static dough.keyword.fixture.KeywordFixture.OUTSIDE_ALONE;
import static dough.login.domain.type.RoleType.MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private QuestService questService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("피드백을 성공적으로 생성할 수 있다.")
    @Test
    void createFeedbackSuccess() {
        // given
        Long questId = 1L;
        FeedbackRequest feedbackRequest = new FeedbackRequest("png1", 3);

        Member member = new Member(1L, "JohnDoe", "john123", SocialLoginType.KAKAO, "john@example.com",
                "Developer", "Male", 1990, ENTHUSIAST, MEMBER);
        SelectedQuest selectedQuest = new SelectedQuest(member, new Quest(1L, "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기", QuestType.DAILY, 3, ENTHUSIAST, OUTSIDE_ALONE));
        Feedback feedback = new Feedback(member, selectedQuest, feedbackRequest.getImageUrl(), feedbackRequest.getDifficulty());

        when(selectedQuestRepository.findByQuestId(questId)).thenReturn(Optional.of(selectedQuest));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // when
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(questId, feedbackRequest);

        // then
        assertEquals(feedback.getId(), feedbackResponse.getId());
        assertEquals(feedback.getImageUrl(), feedbackResponse.getImageUrl());
        assertEquals(feedback.getDifficulty(), feedbackResponse.getDifficulty());
        assertEquals(feedback.getMember().getId(), feedbackResponse.getMemberId());
        assertEquals(feedback.getSelectedQuest().getId(), feedbackResponse.getSelectedQuestId());

        // Mock된 questService가 completeSelectedQuestWithFeedback을 호출할 때 상태 변경 검증
        verify(questService, times(1)).completeSelectedQuestWithFeedback(selectedQuest, feedback);

        // 상태 변경 검증
        verify(questService, times(1)).completeSelectedQuestWithFeedback(selectedQuest, feedback);
        selectedQuest.AddFeedbackToSelectedQuest(feedback);
        assertEquals(QuestStatus.COMPLETED, selectedQuest.getQuestStatus());
    }

    @DisplayName("존재하지 않는 questId로 피드백을 생성할 때 예외가 발생한다.")
    @Test
    void createFeedbackQuestNotFound() {
        // given
        Long questId = 1L;
        FeedbackRequest feedbackRequest = new FeedbackRequest("png2", 3);

        when(selectedQuestRepository.findByQuestId(questId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> feedbackService.createFeedback(questId, feedbackRequest));
    }

    @DisplayName("존재하지 않는 memberId로 피드백을 생성할 때 예외가 발생한다.")
    @Test
    void createFeedbackMemberNotFound() {
        // given
        Long questId = 1L;
        FeedbackRequest feedbackRequest = new FeedbackRequest("png3", 3);

        Member member = new Member(1L, "JohnDoe", "john123", SocialLoginType.APPLE, "john@example.com",
                "Developer", "Male", 1990, ENTHUSIAST, MEMBER);
        SelectedQuest selectedQuest = new SelectedQuest(member, new Quest(1L, "점심시간, 몸과 마음을 건강하게 유지하며",
                "15분 운동하기", QuestType.DAILY, 3, ENTHUSIAST, INSIDE_ALONE));

        when(selectedQuestRepository.findByQuestId(questId)).thenReturn(Optional.of(selectedQuest));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> feedbackService.createFeedback(questId, feedbackRequest));
    }
}
