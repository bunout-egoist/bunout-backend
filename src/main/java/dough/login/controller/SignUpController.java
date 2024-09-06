package dough.login.controller;

import dough.global.exception.ExceptionResponse;
import dough.login.config.jwt.TokenProvider;
import dough.login.dto.request.SignUpRequest;
import dough.login.service.SignUpService;
import dough.member.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SignUpController {

    private final TokenProvider tokenProvider;
    private final SignUpService signUpService;

    @PostMapping("/api/v1/signup/info")
    public ResponseEntity<?> signupInfo(@RequestBody SignUpRequest signUpRequest) {
        String accessToken = signUpRequest.getAccessToken();

        if(!tokenProvider.validToken(accessToken)) {
            return ResponseEntity.status(401).body(new ExceptionResponse(401, "Invalid Token"));
        }

        MemberInfoResponse memberInfoResponse = signUpService.updateMemberInfo(signUpRequest);
        return ResponseEntity.ok(memberInfoResponse);
    }
}
