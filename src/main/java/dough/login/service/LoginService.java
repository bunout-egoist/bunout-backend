package dough.login.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.BadRequestException;
import dough.global.exception.LoginException;
import dough.level.domain.Level;
import dough.level.domain.repository.LevelRepository;
import dough.login.domain.LoginInfo;
import dough.login.domain.MemberInfo;
import dough.login.dto.request.SignUpRequest;
import dough.login.dto.response.AccessTokenResponse;
import dough.login.dto.response.LoginResponse;
import dough.login.infrastructure.jwt.TokenExtractor;
import dough.login.infrastructure.jwt.TokenProvider;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import dough.notification.domain.Notification;
import dough.notification.domain.repository.NotificationRepository;
import dough.notification.domain.type.NotificationType;
import dough.pushNotification.dto.request.FcmTokenRequest;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static dough.global.exception.ExceptionCode.*;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.APPLE;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;
    private final MemberRepository memberRepository;
    private final LevelRepository levelRepository;
    private final TokenProvider tokenProvider;
    private final TokenExtractor tokenExtractor;
    private final BurnoutRepository burnoutRepository;
    private final QuestRepository questRepository;
    private final NotificationRepository notificationRepository;

    public LoginResponse login(final String code, final FcmTokenRequest fcmTokenRequest) {
        final LoginInfo loginInfo = kakaoLoginService.login(code);
        return saveMember(loginInfo, fcmTokenRequest.getFcmToken());
    }

    public LoginResponse login(final String idToken, final String authorizationCode, final FcmTokenRequest fcmTokenRequest) {
        final LoginInfo loginInfo = appleLoginService.login(idToken, authorizationCode);
        return saveMember(loginInfo, fcmTokenRequest.getFcmToken());
    }

    public void logout(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        member.updateRefreshToken(null);
        member.updateNotificationToken(null);

        memberRepository.save(member);
    }

    private MemberInfo findOrCreateMember(final LoginInfo loginInfo) {
        return memberRepository.findBySocialLoginId(loginInfo.getSocialLoginId())
                .map(member -> new MemberInfo(member, false))
                .orElseGet(() -> createMember(loginInfo));
    }

    private MemberInfo createMember(final LoginInfo loginInfo) {
        final Level level = levelRepository.findByLevel(1)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_LEVEL_ID));

        final Member member = new Member(
                loginInfo.getSocialLoginId(),
                loginInfo.getSocialLoginType(),
                MEMBER,
                level,
                loginInfo.getAppleToken()
        );

        return new MemberInfo(member, true);
    }

    private LoginResponse saveMember(final LoginInfo loginInfo, final String notificationToken) {
        final MemberInfo memberInfo = findOrCreateMember(loginInfo);
        final Member member = memberInfo.getMember();

        final String refreshToken = tokenProvider.generateRefreshToken();

        member.updateRefreshToken(refreshToken);
        member.updateNotificationToken(notificationToken);
        memberRepository.save(member);

        final String memberAccessToken = tokenProvider.generateAccessToken(member.getId().toString());

        return LoginResponse.of(memberAccessToken, member, memberInfo.getIsNewMember());
    }

    public void signout(final Long memberId) throws IOException {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final String refreshToken = member.getRefreshToken();

        if (!refreshToken.isEmpty()) {
            if (member.getSocialLoginType().equals(APPLE)) {
                appleLoginService.revoke(member.getAppleToken());
            }
            member.updateRefreshToken(null);
        }

        memberRepository.delete(member);
    }

    public MemberInfoResponse completeSignup(final Long memberId, final SignUpRequest signUpRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Burnout burnout = burnoutRepository.findById(signUpRequest.getBurnoutId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_BURNOUT_ID));

        final Quest fixedQuest = questRepository.findById(signUpRequest.getFixedQuestId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_QUEST_ID));

        member.updateMember(
                signUpRequest.getNickname(),
                signUpRequest.getGender(),
                signUpRequest.getBirthYear(),
                signUpRequest.getOccupation(),
                burnout,
                fixedQuest
        );

        createAllNotifications(member);

        return MemberInfoResponse.of(memberRepository.save(member));
    }

    private void createAllNotifications(final Member member) {
        if (member.getNotifications().isEmpty()) {
            final List<Notification> notifications = Arrays.stream(NotificationType.values())
                    .map(notificationType -> new Notification(member, notificationType))
                    .toList();

            notificationRepository.saveAll(notifications);
        }
    }

    public AccessTokenResponse renewAccessToken() {
        final String accessToken = tokenExtractor.getAccessToken();
        final String refreshToken = tokenExtractor.getRefreshToken();

        if (tokenProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken)) {
            final Member member = memberRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

            final String newAccessToken = tokenProvider.generateAccessToken(member.getId().toString());
            return new AccessTokenResponse(newAccessToken);
        }

        if (tokenProvider.isValidRefreshAndValidAccess(refreshToken, accessToken)) {
            return new AccessTokenResponse(accessToken);
        }

        throw new LoginException(FAIL_TO_RENEW_ACCESS_TOKEN);
    }
}