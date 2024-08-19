package dough.signout.service;

import dough.feedback.domain.Feedback;
import dough.feedback.domain.repository.FeedbackRepository;
import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.quest.domain.SelectedQuest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.repository.SelectedQuestRepository;
import dough.signout.dto.request.SignoutRequestDTO;
import dough.signout.dto.response.SignoutResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
public class SignoutService {

    private final MemberRepository memberRepository;
    private final QuestRepository questRepository;
    private final NotificationRepository notificationRepository;
    private final TokenProvider tokenProvider;
    private final SelectedQuestRepository selectedQuestRepository;
    private final FeedbackRepository feedbackRepository;

    public SignoutResponseDTO signout(SignoutRequestDTO signoutRequestDTO) {
        // 토큰 인증 및 파싱
        String token =  signoutRequestDTO.getToken();
        Long memberId = null;

        if (!tokenProvider.validToken(token)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        memberId = tokenProvider.getMemberIdFromToken(token);

        // 멤버 객체 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 멤버 삭제하기
        memberRepository.delete(member);

        // 퀘스트 삭제해 버리기
        List<SelectedQuest> selectedQuestList = member.getSelectedQuests();
        if (!selectedQuestList.isEmpty()) {
            selectedQuestList
                    .forEach(sq -> selectedQuestRepository.delete(sq));
        }

        // 피드백 삭제해 버리기
        List<Feedback> feedbackList = member.getFeedbacks();
        if (!feedbackList.isEmpty()) {
            feedbackList
                    .forEach(fd -> feedbackRepository.delete(fd));
        }

        // 알림 삭제해 버리기
        List<Notification> notificationList = member.getNotifications();
        if(!notificationList.isEmpty()) {
            notificationList
                    .forEach(nl -> notificationRepository.delete(nl));
        }

        return new SignoutResponseDTO(memberId);
    }
}
