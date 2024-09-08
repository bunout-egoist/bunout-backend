package dough.login.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.repository.LevelRepository;
import dough.login.LoginApiClient;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.response.LoginResponse;
import dough.login.dto.response.KakaoMemberResponse;
import dough.login.dto.response.KakaoTokenResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Duration;

import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;
import static dough.login.domain.type.RoleType.MEMBER;
import static dough.login.domain.type.SocialLoginType.KAKAO;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoLoginService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final LoginApiClient loginApiClient;
    private final LevelRepository levelRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public LoginResponse kakaoLogin(final String code) {

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);
        params.add("client_id", clientId);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        final KakaoTokenResponse kakaoTokenResponse = loginApiClient.getKakaoToken(params);

        final LoginResponse loginResponse = getKakaoMember(kakaoTokenResponse.getAccessToken());
        return loginResponse;
    }

    public LoginResponse getKakaoMember(final String accessToken) {
        final KakaoMemberResponse kakaoMemberResponse = loginApiClient.getKakaoMemberInfo("Bearer " + accessToken);

        final Level level = levelRepository.findByLevel(1)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_LEVEL_ID));

        // TODO 해당 로직 수정, 기존 코드에 합치기
        final Member member = memberRepository.findBySocialLoginId(clientId)
                .orElseGet(() -> {
                    final Member newMember = new Member(
                            kakaoMemberResponse.getId().toString(),
                            KAKAO,
                            MEMBER,
                            level
                    );
                    return newMember;
                });

        final String memberAccessToken = tokenProvider.generateToken(member, Duration.ofHours(1));
        final String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

        return LoginResponse.of(memberAccessToken, member, false);
    }
}