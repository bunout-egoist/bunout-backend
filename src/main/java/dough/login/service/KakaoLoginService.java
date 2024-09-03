package dough.login.service;

import dough.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.login.dto.response.KakaoTokenResponse;
import dough.login.dto.response.KakaoTokenResponseDto;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

import static dough.global.exception.ExceptionCode.INVALID_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class KakaoLoginService {

    private final MemberRepository memberRepository;
    private final LoginService loginService;
    private final TokenProvider tokenProvider;
    private final WebClient webClient = WebClient.create();
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private  String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private  String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private  String kakaoRedirectUri;

    public KakaoTokenResponse kakaoLogin(Map<String, String> request) {
        String code = request.get("code");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        WebClient wc = WebClient.create("https://kauth.kakao.com");
        String response = wc.post()
                .uri("/oauth/token")
                .body(BodyInserters.fromFormData(params))
                .header("Content-type","application/x-www-form-urlencoded;charset=utf-8" ) //요청 헤더
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenResponseDto kakaoToken = null;

        try {
            kakaoToken = objectMapper.readValue(response, KakaoTokenResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String accessToken = kakaoToken.getAccessToken();
        String refreshToken = kakaoToken.getRefreshToken();

        Map<String, Object> userInfoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("kapi.kakao.com")
                        .path("/v2/user/me")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (userInfoResponse == null) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        String socialLoginId = String.valueOf(userInfoResponse.get("id"));

        Member member;
        if(memberRepository.existsBySocialLoginId(socialLoginId)) {
            member = loginService.findBySocialLoginId(socialLoginId);
        }
        else {
            member = loginService.createMember(socialLoginId, SocialLoginType.KAKAO, null, RoleType.MEMBER);
        }

        RefreshToken newRefreshToken = new RefreshToken(member, refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(1));
        String jwtRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));

        return new KakaoTokenResponse(jwtToken, jwtRefreshToken);
    }
}
