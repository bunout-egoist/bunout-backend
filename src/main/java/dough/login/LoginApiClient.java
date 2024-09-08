package dough.login;

import dough.login.dto.response.AppleMemberResponse;
import dough.login.dto.response.KakaoMemberResponse;
import dough.login.dto.response.KakaoTokenResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public interface LoginApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponse getKakaoToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange("https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse getKakaoMemberInfo(@RequestHeader(name = AUTHORIZATION) String bearerToken);

    @GetExchange("https://www.googleapis.com/userinfo/v2/me")
    AppleMemberResponse getAppleMemberInfo(@RequestHeader(name = AUTHORIZATION) final String bearerToken);
}
