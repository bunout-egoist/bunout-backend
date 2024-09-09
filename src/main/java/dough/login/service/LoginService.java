package dough.login.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.global.exception.AuthException;
import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.repository.LevelRepository;
import dough.login.LoginApiClient;
import dough.login.config.jwt.TokenExtractor;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.LoginInfo;
import dough.login.domain.MemberInfo;
import dough.login.dto.request.SignUpRequest;
import dough.login.dto.response.AccessTokenResponse;
import dough.login.dto.response.LoginResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static dough.global.exception.ExceptionCode.*;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.APPLE;
import static dough.login.domain.type.SocialLoginType.KAKAO;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final LoginApiClient loginApiClient;
    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;
    private final MemberRepository memberRepository;
    private final LevelRepository levelRepository;
    private final TokenProvider tokenProvider;
    private final TokenExtractor tokenExtractor;
    private final BurnoutRepository burnoutRepository;
    private final QuestRepository questRepository;
    private final NotificationRepository notificationRepository;

    public LoginResponse login(final String provider, final String code) {
        if (provider.equals(KAKAO.getCode())) {
            return saveMember(kakaoLoginService.login(code));
        } else if (provider.equals(APPLE.getCode())) {
            return saveMember(appleLoginService.login(code));
        } else {
            throw new BadRequestException(FAIL_SOCIAL_LOGIN);
        }
    }

    public void logout(final Long memberId) {
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        member.updateRefreshToken(null);

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
                level
        );

        return new MemberInfo(member, true);
    }

    private LoginResponse saveMember(final LoginInfo loginInfo) {
        final MemberInfo memberInfo = findOrCreateMember(loginInfo);
        final Member member = memberInfo.getMember();

        final String memberAccessToken = tokenProvider.generateAccessToken(member.getId().toString());
        final String refreshToken = tokenProvider.generateRefreshToken();

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

        return LoginResponse.of(memberAccessToken, member, false);
    }

    public void signout(final Long memberId) throws IOException {
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final String refreshToken = member.getRefreshToken();

        // TODO 로직 확인
        if (!refreshToken.isEmpty()) {
            if (member.getSocialLoginType().equals(APPLE)) {
                final String clientSecret = appleLoginService.makeClientSecret();
                loginApiClient.revokeToken(clientSecret, refreshToken, "com.bunout.services");
            }
            member.updateRefreshToken(null);
        }

        memberRepository.delete(member);
    }

    public MemberInfoResponse completeSignup(final Long memberId, final SignUpRequest signUpRequest) {
        final Member member = memberRepository.findMemberById(memberId)
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
        final List<Notification> notifications = Arrays.stream(NotificationType.values())
                .map(notificationType -> new Notification(member, notificationType))
                .toList();

        notificationRepository.saveAll(notifications);
    }

    public AccessTokenResponse renewAccessToken(final Long memberId) {
        final String refreshToken = tokenExtractor.getRefreshToken();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        if (!member.getRefreshToken().equals(refreshToken)) {
            tokenProvider.validToken(refreshToken);
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        final String accessToken = tokenProvider.generateAccessToken(memberId.toString());

        return new AccessTokenResponse(accessToken);
    }
}
