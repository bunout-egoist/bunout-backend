package dough.member.controller;

import dough.member.dto.request.BurnoutTypeRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberInfoResponse;
import dough.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                               @RequestBody @Valid final MemberInfoRequest memberInfoRequest
    ) {
        final MemberInfoResponse memberInfoResponse = memberService.updateMemberInfo(memberId, memberInfoRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping("/{memberId}/burnoutType")
    public ResponseEntity<Void> changeBurnoutType(@PathVariable("memberId") final Long memberId,
                                                  @RequestBody @Valid final BurnoutTypeRequest burnoutTypeRequest
    ) {
        memberService.changeBurnoutType(memberId, burnoutTypeRequest);
        return ResponseEntity.noContent().build();
    }
}
