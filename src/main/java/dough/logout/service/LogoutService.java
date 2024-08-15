package dough.logout.service;

import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.logout.dto.request.DeleteAccessTokenRequest;
import dough.logout.dto.response.DeleteAccessTokenResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dough.global.exception.ExceptionCode.INVALID_REQUEST;
import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public DeleteAccessTokenResponse logout(DeleteAccessTokenRequest deleteAccessTokenRequest) {

        String accessToken = deleteAccessTokenRequest.getAccessToken();

        if (!tokenProvider.validToken(accessToken)) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        Long memberId = tokenProvider.getMemberIdFromToken(accessToken);

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        if(refreshToken != null) {
            refreshTokenRepository.delete(refreshToken);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        return DeleteAccessTokenResponse.from(member);
    }
}
