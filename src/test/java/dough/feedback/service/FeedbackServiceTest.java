package dough.feedback.service;

import dough.feedback.domain.repository.FeedbackRepository;
import dough.feedback.dto.request.FeedbackRequest;
import dough.feedback.dto.response.FeedbackResponse;
import dough.global.exception.BadRequestException;
import dough.level.domain.MemberLevel;
import dough.level.service.LevelService;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static dough.feedback.fixture.FeedbackFixture.FEEDBACK1;
import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static dough.global.exception.ExceptionCode.NOT_FOUND_SELECTED_QUEST_ID;
import static dough.level.fixture.LevelFixture.LEVEL2;
import static dough.member.fixture.MemberFixture.GOEUN;
import static dough.quest.fixture.SelectedQuestFixture.COMPLETED_QUEST1;
import static dough.quest.fixture.SelectedQuestFixture.IN_PROGRESS_QUEST1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
@Transactional
class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private LevelService levelService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private SelectedQuestRepository selectedQuestRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileService fileService;

    @DisplayName("피드백을 성공적으로 생성할 수 있다.")
    @Test
    void createFeedback() {
        // given
        FeedbackRequest feedbackRequest = new FeedbackRequest(1L, 5);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        final MemberLevel memberLevel = new MemberLevel(GOEUN, LEVEL2, true, 30);

        IN_PROGRESS_QUEST1.updateFeedback(FEEDBACK1);

        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));
        given(selectedQuestRepository.findById(anyLong()))
                .willReturn(Optional.of(IN_PROGRESS_QUEST1));
        given(feedbackRepository.save(any()))
                .willReturn(FEEDBACK1);
        given(selectedQuestRepository.save(any()))
                .willReturn(COMPLETED_QUEST1);
        given(levelService.updateLevel(any()))
                .willReturn(memberLevel);
        given(memberRepository.save(any()))
                .willReturn(memberLevel.getMember());

        // when
        final FeedbackResponse actualResponse = feedbackService.createFeedback(GOEUN.getId(), feedbackRequest, mockFile);

        // then
        FeedbackResponse expectedResponse = FeedbackResponse.of(memberLevel, null);
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @DisplayName("존재하지 않는 선택된 퀘스트 아이디로 피드백을 생성할 때 예외가 발생한다.")
    @Test
    void createFeedbackQuestNotFound() {
        // given
        FeedbackRequest feedbackRequest = new FeedbackRequest(1L, 5);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        given(memberRepository.findMemberById(anyLong()))
                .willReturn(Optional.of(GOEUN));

        // when & then
        assertThatThrownBy(() -> feedbackService.createFeedback(GOEUN.getId(), feedbackRequest, mockFile))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_SELECTED_QUEST_ID.getCode());
    }

    @DisplayName("존재하지 않는 멤버 아이디로 피드백을 생성할 때 예외가 발생한다.")
    @Test
    void createFeedbackMemberNotFound() {
        // given
        FeedbackRequest feedbackRequest = new FeedbackRequest(1L, 5);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        // when & then
        assertThatThrownBy(() -> feedbackService.createFeedback(GOEUN.getId(), feedbackRequest, mockFile))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(NOT_FOUND_MEMBER_ID.getCode());
    }
}
