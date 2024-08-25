package dough.login.service;

import dough.global.exception.UserNotFoundException;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public MemberInfoResponse updateMemberInfo(SignUpRequest signUpRequest) {
        String accessToken = signUpRequest.getAccessToken();
        Long member_id = tokenProvider.getMemberIdFromToken(accessToken);

        final Member member = memberRepository.findMemberById(member_id)
                .orElseThrow(UserNotFoundException::new);

        member.updateMember(
                signUpRequest.getNickname(),
                signUpRequest.getGender(),
                signUpRequest.getBirth_year(),
                signUpRequest.getOccupation()
        );

        return MemberInfoResponse.of(memberRepository.save(member));
    }
}
