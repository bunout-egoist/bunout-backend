package dough.login.service;

import dough.global.exception.BadRequestException;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.login.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findBySocialLoginId(String socialLoginId) {
        return refreshTokenRepository.findBySocialLoginId(socialLoginId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
    }
}