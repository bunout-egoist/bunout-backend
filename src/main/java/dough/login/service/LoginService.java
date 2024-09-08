package dough.login.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.repository.LevelRepository;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.LoginInfo;
import dough.login.domain.MemberInfo;
import dough.login.dto.response.LoginResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static dough.global.exception.ExceptionCode.FAIL_SOCIAL_LOGIN;
import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.APPLE;
import static dough.login.domain.type.SocialLoginType.KAKAO;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;
    private final MemberRepository memberRepository;
    private final LevelRepository levelRepository;
    private final TokenProvider tokenProvider;

    public LoginResponse login(final String provider, final String code) {
        if (provider.equals(KAKAO.getCode())) {
            return saveMember(kakaoLoginService.login(code));
        } else if (provider.equals(APPLE.getCode())) {
            return saveMember(appleLoginService.login(code));
        } else {
            throw new BadRequestException(FAIL_SOCIAL_LOGIN);
        }
    }

    public LoginResponse saveMember(final LoginInfo loginInfo) {
        final MemberInfo memberInfo = findOrCreateMember(loginInfo);
        final Member member = memberInfo.getMember();

        final String memberAccessToken = tokenProvider.generateToken(memberInfo.getMember(), Duration.ofHours(1));
        final String refreshToken = tokenProvider.generateToken(memberInfo.getMember(), Duration.ofDays(14));

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

        return LoginResponse.of(memberAccessToken, member, false);
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
}
