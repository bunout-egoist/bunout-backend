package dough.login.service;

import dough.global.exception.UserNotFoundException;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.member.domain.Member;
import dough.member.domain.repository.MemberRepository;
import dough.member.dto.response.MemberInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static dough.member.fixture.MemberFixture.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@Transactional
class SignUpServiceTest {

    @InjectMocks
    private SignUpService signUpService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProvider tokenProvider;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setup() {
        signUpRequest = new SignUpRequest("validAccessToken", "kimjunhee", "남성", 1990, "직장인");
    }

    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 수 있다.")
    @Test
    void updateMemberInfo_withSignUpRequest() {
        // given
        given(tokenProvider.getUserIdFromToken(anyString())).willReturn("socialLoginId");
        given(memberRepository.findBySocialLoginId(anyString())).willReturn(Optional.of(MEMBER));
        given(memberRepository.save(any(Member.class))).willReturn(MEMBER);

        // when
        MemberInfoResponse response = signUpService.updateMemberInfo(signUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(MEMBER.getId());
        assertThat(response.getNickname()).isEqualTo(MEMBER.getNickname());

        // save 메서드에 전달된 Member 객체 캡처
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(1)).save(memberCaptor.capture());
        Member capturedMember = memberCaptor.getValue();

        // then
        assertThat(capturedMember.getNickname()).isEqualTo(MEMBER.getNickname());
        assertThat(capturedMember.getGender()).isEqualTo(MEMBER.getGender());
        assertThat(capturedMember.getBirthYear()).isEqualTo(MEMBER.getBirthYear());
        assertThat(capturedMember.getOccupation()).isEqualTo(MEMBER.getOccupation());

        verify(tokenProvider, times(1)).getUserIdFromToken(anyString());
        verify(memberRepository, times(1)).findBySocialLoginId(anyString());
    }

    @DisplayName("SignUpRequest를 통해 회원 정보를 업데이트할 때 회원을 찾지 못하면 예외가 발생한다.")
    @Test
    void updateMemberInfo_withSignUpRequest_UserNotFound() {
        // given
        given(tokenProvider.getUserIdFromToken(anyString())).willReturn("socialLoginId");
        given(memberRepository.findBySocialLoginId(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> signUpService.updateMemberInfo(signUpRequest))
                .isInstanceOf(UserNotFoundException.class);

        verify(tokenProvider, times(1)).getUserIdFromToken(anyString());
        verify(memberRepository, times(1)).findBySocialLoginId(anyString());
        verify(memberRepository, times(0)).save(any(Member.class));
    }
}

