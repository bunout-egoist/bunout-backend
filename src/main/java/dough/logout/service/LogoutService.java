package dough.logout.service;

import dough.global.exception.BadRequestException;
import dough.login.config.jwt.TokenProvider;
import dough.login.domain.RefreshToken;
import dough.login.domain.client.AppleClient;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.login.domain.type.SocialLoginType;
import dough.login.service.AppleLoginService;
import dough.logout.dto.request.DeleteAccessTokenRequest;
import dough.logout.dto.response.DeleteAccessTokenResponse;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dough.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final AppleClient appleClient;
    private final AppleLoginService appleLoginService;

    @Transactional
    public DeleteAccessTokenResponse logout(DeleteAccessTokenRequest deleteAccessTokenRequest) {

        String accessToken = deleteAccessTokenRequest.getAccessToken();

        if (!tokenProvider.validToken(accessToken)) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        Long memberId = tokenProvider.getMemberIdFromToken(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        if(refreshToken != null && member.getSocialLoginType() == SocialLoginType.KAKAO) {
            refreshTokenRepository.delete(refreshToken);
        }
        else if(refreshToken != null && member.getSocialLoginType() == SocialLoginType.APPLE) {
            String clientSecret;
            try {
                clientSecret = appleLoginService.makeClientSecret();
                appleClient.revokeToken(clientSecret, refreshToken.getRefreshToken(), "com.bunout.services");
                refreshTokenRepository.delete(refreshToken);
            } catch (Exception e) {
                throw new BadRequestException(INTERNAL_SEVER_ERROR);
            }
        }

        return DeleteAccessTokenResponse.from(member);
    }
}