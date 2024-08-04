package dough.login.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.login.dto.response.ApiResponse;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.login.dto.response.KakaoTokenResponseDto;
import dough.login.dto.response.TokensResponse;
import dough.login.service.LoginService;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final LoginService memberService;
    private final TokenProvider tokenProvider;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private  String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private  String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private  String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private  String kakaoAccessTokenUri;

    private final WebClient webClient = WebClient.create();
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<TokensResponse>> loginWithKakao(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        //request
        WebClient wc = WebClient.create("https://kauth.kakao.com");
        String response = wc.post()
                .uri("/oauth/token")
                .body(BodyInserters.fromFormData(params))
                .header("Content-type","application/x-www-form-urlencoded;charset=utf-8" ) //요청 헤더
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //json형태로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenResponseDto kakaoToken = null;

        try {
            kakaoToken = objectMapper.readValue(response, KakaoTokenResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String accessToken = kakaoToken.getAccessToken();
        String refreshToken = kakaoToken.getRefreshToken();

        // 받은 토큰으로 사용자 정보 요청하기
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to retrieve user info from Kakao"));
        }

        //////////////////

        // 카카오에서 사용자 정보 가져오기
        String socialLoginId = String.valueOf(userInfoResponse.get("id")); // 카카오 사용자 ID를 가져옴

        Member member;
        try {
            member = memberService.findBySocialLoginId(socialLoginId);
        } catch (IllegalArgumentException e) {
            member = memberService.createMember(socialLoginId, SocialLoginType.KAKAO, null, RoleType.MEMBER);
        }

        // 리프레시 토큰 저장 by memberId
        RefreshToken newRefreshToken = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        // 멤버 정보로 JWT 토큰 생성
        String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(1));

        TokensResponse tokensResponse = new TokensResponse(jwtToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success("tokens", tokensResponse));
    }
}
