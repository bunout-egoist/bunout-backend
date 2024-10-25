package dough.login.service;

import dough.login.infrastructure.oauth.LoginApiClient;
import dough.login.domain.LoginInfo;
import dough.login.dto.response.KakaoMemberResponse;
import dough.login.dto.response.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static dough.login.domain.type.SocialLoginType.KAKAO;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoLoginService {

    private final LoginApiClient loginApiClient;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public LoginInfo login(final String code) {
        final KakaoTokenResponse kakaoTokenResponse = loginApiClient.getKakaoToken(tokenRequestParams(code));
        log.info(kakaoTokenResponse.getAccessToken());
        final KakaoMemberResponse kakaoMemberResponse = loginApiClient.getKakaoMemberInfo("Bearer " + kakaoTokenResponse.getAccessToken());
        log.info(kakaoMemberResponse.getId().toString());
        return new LoginInfo(kakaoMemberResponse.getId().toString(), KAKAO);
    }

    private MultiValueMap<String, String> tokenRequestParams(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);
        params.add("client_id", clientId);
        params.add("code", code);
        params.add("client_secret", clientSecret);
        return params;
    }
}