package dough.feedback.service;

import dough.feedback.domain.Feedback;
import dough.feedback.domain.repository.FeedbackRepository;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.global.exception.BadRequestException;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.feedback.fixture.FeedbackFixture.FEEDBACK1;
import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static dough.global.exception.ExceptionCode.NOT_FOUND_SELECTED_QUEST_ID;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.SelectedQuestFixture.COMPLETED_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("피드백을 성공적으로 생성할 수 있다.")
    @Test
    void createFeedback() {
        // given
        final FeedbackRequest feedbackRequest = new FeedbackRequest(
                "png1",
                1L,
                5
        );

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(selectedQuestRepository.findById(anyLong()))
                .willReturn(Optional.of(COMPLETED_QUEST1));
        given(feedbackRepository.save(any(Feedback.class)))
                .willReturn(FEEDBACK1);

        // when
        final FeedbackResponse actualResponse = feedbackService.createFeedback(GOEUN.getId(), feedbackRequest);

        // then
        verify(memberRepository).findById(any());
        verify(selectedQuestRepository).findById(anyLong());
        verify(feedbackRepository).save(any());
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(FeedbackResponse.from(GOEUN));
    }

    @DisplayName("존재하지 않는 선택된 퀘스트 아이디로 피드백을 생성할 때 예외가 발생한다.")
    @Test
    void createFeedbackQuestNotFound() {
        // given
        final FeedbackRequest feedbackRequest = new FeedbackRequest(
                "png1",
                1L,
                5
        );

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(GOEUN));

        // when & then
        assertThatThrownBy(() -> feedbackService.createFeedback(GOEUN.getId(), feedbackRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_SELECTED_QUEST_ID.getCode());
    }

    @DisplayName("존재하지 않는 멤버 아이디로 피드백을 생성할 때 예외가 발생한다.")
    @Test
    void createFeedbackMemberNotFound() {
        // given
        final FeedbackRequest feedbackRequest = new FeedbackRequest(
                "png1",
                1L,
                5
        );

        // when & then
        assertThatThrownBy(() -> feedbackService.createFeedback(GOEUN.getId(), feedbackRequest))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_MEMBER_ID.getCode());
    }
}
