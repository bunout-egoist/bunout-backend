package dough.login.service;

import dough.global.exception.UserNotFoundException;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public MemberInfoResponse updateMemberInfo(SignUpRequest signUpRequest) {
        String accessToken = signUpRequest.getAccessToken();
        String socialLoginId = tokenProvider.getUserIdFromToken(accessToken);

        final Member member = memberRepository.findBySocialLoginId(socialLoginId)
                .orElseThrow(UserNotFoundException::new);

        member.updateMember(
                member.getNickname(),
                member.getGender(),
                member.getBirthYear(),
                member.getOccupation()
        );

        return MemberInfoResponse.from(memberRepository.save(member));
    }
}
