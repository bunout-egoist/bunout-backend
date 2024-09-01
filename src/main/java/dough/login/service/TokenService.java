package dough.login.service;


import dough.global.exception.BadRequestException;
import dough.login.config.jwt.JwtHeaderUtil;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.dto.response.TokensResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static dough.global.exception.ExceptionCode.INTERNAL_SEVER_ERROR;
import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    public String createNewAccessToken(String refreshToken) {

        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }
        Long memberId = refreshTokenService.findByRefreshToken(refreshToken).getMember().getId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        return tokenProvider.generateToken(member, Duration.ofHours(1));
    }

    public TokensResponse refreshTokens(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new BadRequestException(INTERNAL_SEVER_ERROR);
        }

        Long memberId = refreshTokenService.findByRefreshToken(refreshToken).getMember().getId();
        final Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        String newAccessToken = tokenProvider.generateToken(member, Duration.ofHours(1));
        String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(14));

        RefreshToken savedRefreshToken = refreshTokenService.findByMemberId(member.getId());

        savedRefreshToken.update(newRefreshToken);
        refreshTokenService.save(savedRefreshToken);

        return new TokensResponse(newAccessToken, newRefreshToken);
    }

    public Long getMemberId() {
        final String accessToken = JwtHeaderUtil.getAccessToken();
        return tokenProvider.getMemberIdFromToken(accessToken);
    }
}