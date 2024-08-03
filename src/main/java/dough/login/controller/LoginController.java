package dough.login.controller;

import dough.login.config.jwt.TokenProvider;
import dough.login.dto.response.ApiResponse;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.login.service.LoginService;
import dough.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final WebClient webClient = WebClient.create();

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<String>> loginWithKakao(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        Map<String, Object> tokenResponse = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("kauth.kakao.com")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("redirect_uri", kakaoRedirectUri)
                        .queryParam("code", code)
                        .queryParam("client_secret", kakaoClientSecret)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to retrieve access token from Kakao"));
        }

        // 카카오로부터 엑세스 토큰을 가져온다.
        String accessToken = (String) tokenResponse.get("access_token");

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
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoResponse.get("kakao_account");
        String socialLoginId = String.valueOf(userInfoResponse.get("id")); // 카카오 사용자 ID를 가져옴
        String nickname = (String) kakaoAccount.get("nickname"); // 카카오 닉네임 가져옴

        Member member;
        try {
            member = memberService.findBySocialLoginId(socialLoginId);
        } catch (IllegalArgumentException e) {
            member = memberService.createMember(socialLoginId, SocialLoginType.KAKAO, nickname, RoleType.MEMBER);
        }

        // 멤버 정보로 JWT 토큰 생성
        String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(1));

        return ResponseEntity.ok(ApiResponse.success("token", jwtToken));
    }
}
