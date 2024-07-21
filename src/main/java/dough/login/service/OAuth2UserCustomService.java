package dough.login.service;

import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class  OAuth2UserCustomService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (registrationId.equals("kakao")) {

        }
        else if(registrationId.equals("google")) {

        } else {
            return null;
        }
        return user;
    }
}