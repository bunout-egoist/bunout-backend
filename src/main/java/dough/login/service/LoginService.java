package dough.login.service;

import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;

    public Member createMember(String socialLoginId, SocialLoginType socialLoginType, String nickname, RoleType roleType) {
        Member member = new Member(
                null,
                nickname,
                socialLoginId,
                socialLoginType,
                null,
                null,
                null,
                null,
                null,
                roleType
        );
        return memberRepository.save(member);
    }

    /**
     * 있으면 저장, 없으면 update
     * @param oAuth2User 카카오에서 가져온 사용자
     * @return
     */
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
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
