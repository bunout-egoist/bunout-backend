package dough.login.service;

import dough.global.exception.BadRequestException;
import dough.login.config.jwt.JwtHeaderUtil;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.response.TokensResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }
        final Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        return tokenProvider.generateToken(member, Duration.ofHours(1));
    }

    public TokensResponse refreshTokens(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid token");
        }

        final Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final String newAccessToken = tokenProvider.generateToken(member, Duration.ofHours(1));
        final String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));

        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        return new TokensResponse(newAccessToken, newRefreshToken);
    }

    public Long getMemberId() {
        final String accessToken = JwtHeaderUtil.getAccessToken();
        return tokenProvider.getMemberIdFromToken(accessToken);
    }
}