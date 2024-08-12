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
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member user = memberService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(1));
    }

    public TokensResponse refreshTokens(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member user = memberService.findById(userId);

        String newAccessToken = tokenProvider.generateToken(user, Duration.ofHours(1));
        String newRefreshToken = tokenProvider.generateToken(user, Duration.ofDays(14));

        RefreshToken savedRefreshToken = refreshTokenService.findByUserId(userId);

        savedRefreshToken.update(newRefreshToken);
        refreshTokenService.save(savedRefreshToken);

        return new TokensResponse(newAccessToken, newRefreshToken);
    }
}