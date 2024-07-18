package dough.member.controller;

import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    public final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@PathVariable final Long memberId) {
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(memberInfoResponse);
    }
}
