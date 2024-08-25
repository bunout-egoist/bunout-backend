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
    private final NotificationRepository notificationRepository;
    private final TokenProvider tokenProvider;
    private final SelectedQuestRepository selectedQuestRepository;
    private final FeedbackRepository feedbackRepository;

    public SignoutResponseDTO signout(SignoutRequestDTO signoutRequestDTO) {
        String token =  signoutRequestDTO.getToken();
        Long memberId = null;

        if (!tokenProvider.validToken(token)) {
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }

        memberId = tokenProvider.getMemberIdFromToken(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        memberRepository.delete(member);

        List<SelectedQuest> selectedQuestList = member.getSelectedQuests();
        if (!selectedQuestList.isEmpty()) {
            selectedQuestList
                    .forEach(sq -> selectedQuestRepository.delete(sq));
        }

        List<Feedback> feedbackList = member.getFeedbacks();
        if (!feedbackList.isEmpty()) {
            feedbackList
                    .forEach(fd -> feedbackRepository.delete(fd));
        }

        List<Notification> notificationList = member.getNotifications();
        if(!notificationList.isEmpty()) {
            notificationList
                    .forEach(nl -> notificationRepository.delete(nl));
        }

        return new SignoutResponseDTO(memberId);
    }
}
