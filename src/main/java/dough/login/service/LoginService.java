package dough.login.service;

import dough.burnout.domain.Burnout;
import dough.burnout.domain.repository.BurnoutRepository;
import dough.keyword.domain.Keyword;
import dough.keyword.domain.repository.KeywordRepository;
import dough.login.domain.type.RoleType;
import dough.login.domain.type.SocialLoginType;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.quest.domain.Quest;
import dough.quest.domain.repository.QuestRepository;
import dough.quest.domain.type.QuestType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final QuestRepository questRepository;
    private final KeywordRepository keywordRepository;
    private final BurnoutRepository burnoutRepository;

    public Member createMember(String socialLoginId, SocialLoginType socialLoginType, String nickname, RoleType roleType) {
        Burnout burnout = new Burnout(1L, "호빵");
        burnoutRepository.save(burnout);
        Keyword keyword = new Keyword(true, false);
        keywordRepository.save(keyword);
        Quest quest = new Quest("quest", "do it", QuestType.DAILY, 1, burnout, keyword);
        questRepository.save(quest);

        Member member = new Member(
                null,
                nickname,
                socialLoginId,
                socialLoginType,
                null,
                null,
                null,
                null,
                burnout,
                quest,
                roleType
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
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

}
