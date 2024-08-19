package dough.login.service;

import dough.global.exception.BadRequestException;
import dough.level.domain.Level;
import dough.level.domain.repository.LevelRepository;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import static dough.global.exception.ExceptionCode.NOT_FOUND_LEVEL_ID;
import static dough.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final LevelRepository levelRepository;

    public Member createMember(String socialLoginId, SocialLoginType socialLoginType, String nickname, RoleType roleType) {
        final Level level = levelRepository.findByLevel(1)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_LEVEL_ID));
        final Member member = new Member(
                null,
                nickname,
                socialLoginId,
                socialLoginType,
                null,
                null,
                null,
                null,
                null,
                roleType,
                level
        );

        return memberRepository.save(member);
    }

    public Member saveOrUpdate(OAuth2User oAuth2User) {
        String socialLoginId = oAuth2User.getName();
        String nickname = (String) oAuth2User.getAttributes().get("nickname");

        return memberRepository.findBySocialLoginId(socialLoginId)
                .map(entity -> {
                    entity.updateMember(nickname);
                    return entity;
                })
                .orElseGet(() -> {
                    Member newUser = this.createMember(socialLoginId, SocialLoginType.KAKAO, nickname, RoleType.MEMBER);
                    return memberRepository.save(newUser);
                });
    }

    public Member findBySocialLoginId(String socialLoginId) {
        return memberRepository.findBySocialLoginId(socialLoginId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
    }
}
