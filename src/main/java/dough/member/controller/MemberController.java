package dough.member.controller;

import dough.member.domain.Member;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    public final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@PathVariable("memberId") final Long memberId) {
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<MemberInfoResponse> updateMemberInfo(@PathVariable("memberId") final Long memberId,
                                                               @RequestBody @Valid final MemberInfoRequest memberInfoRequest) {
        final MemberInfoResponse memberInfoResponse = memberService.updateMemberInfo(memberId, memberInfoRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Member member = memberService.findById(id);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User doesn't exist"));
        }
    }
}
