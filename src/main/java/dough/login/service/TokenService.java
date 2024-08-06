package dough.login.service;


import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.dto.response.TokensResponse;
import dough.member.domain.Member;
import dough.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken) {
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }
        String socialLoginId = refreshTokenService.findByRefreshToken(refreshToken).getSocialLoginId();
        Member member = memberService.findBySocialLoginId(socialLoginId);

        return tokenProvider.generateToken(member, Duration.ofHours(1));
    }

    public TokensResponse refreshTokens(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String socialLoginId = refreshTokenService.findByRefreshToken(refreshToken).getSocialLoginId();
        Member member = memberService.findBySocialLoginId(socialLoginId);

        String newAccessToken = tokenProvider.generateToken(member, Duration.ofHours(1));
        String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));

        RefreshToken savedRefreshToken = refreshTokenService.findBySocialLoginId(socialLoginId);

        savedRefreshToken.update(newRefreshToken);
        refreshTokenService.save(savedRefreshToken);

        return new TokensResponse(newAccessToken, newRefreshToken);
    }
}