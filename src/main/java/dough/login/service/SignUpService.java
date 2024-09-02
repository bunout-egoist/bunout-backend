package dough.login.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.global.exception.UserNotFoundException;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import dough.notification.NotificationRepository;
import dough.notification.domain.Notification;
import dough.notification.domain.type.NotificationType;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static dough.global.exception.ExceptionCode.NOT_FOUND_BURNOUT_ID;
import static dough.global.exception.ExceptionCode.NOT_FOUND_QUEST_ID;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final BurnoutRepository burnoutRepository;
    private final QuestRepository questRepository;

    private final NotificationRepository notificationRepository;

    public MemberInfoResponse updateMemberInfo(SignUpRequest signUpRequest) {
        String accessToken = signUpRequest.getAccessToken();
        Long member_id = tokenProvider.getMemberIdFromToken(accessToken);

        final Member member = memberRepository.findMemberById(member_id)
                .orElseThrow(UserNotFoundException::new);

        member.updateMember(
                signUpRequest.getNickname(),
                signUpRequest.getGender(),
                signUpRequest.getBirth_year(),
                signUpRequest.getOccupation()
        );

        Burnout burnout = burnoutRepository.findById(signUpRequest.getBunoutId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));
        member.updateBurnout(burnout, LocalDate.now());

        Quest fixedQuest = questRepository.findById(signUpRequest.getFixedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));
        member.updateFixedQuest(fixedQuest, LocalDate.now());

        return MemberInfoResponse.of(memberRepository.save(member));
    }

    private void createAllNotifications(final Member member) {
        final List<Notification> notifications = Arrays.stream(NotificationType.values())
                .map(notificationType -> new Notification(member, notificationType))
                .toList();

        notificationRepository.saveAll(notifications);
    }
}