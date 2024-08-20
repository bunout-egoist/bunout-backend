package dough.member.controller;

import dough.member.domain.Member;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberAttendanceResponse;
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
                                                               @RequestBody @Valid final MemberInfoRequest memberInfoRequest
    ) {
        final MemberInfoResponse memberInfoResponse = memberService.updateMemberInfo(memberId, memberInfoRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping("/{memberId}/burnout")
    public ResponseEntity<Void> updateBurnout(@PathVariable("memberId") final Long memberId,
                                              @RequestBody @Valid final BurnoutRequest burnoutRequest
    ) {
        memberService.updateBurnout(memberId, burnoutRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/fixed")
    public ResponseEntity<Void> updateFixedQuest(@PathVariable("memberId") final Long memberId,
                                                 @RequestBody @Valid final FixedQuestRequest fixedQuestRequest
    ) {
        memberService.updateFixedQuest(memberId, fixedQuestRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/attendance")
    public ResponseEntity<MemberAttendanceResponse> checkAttendance(@PathVariable("memberId") final Long memberId) {
        final MemberAttendanceResponse memberAttendanceResponse = memberService.checkAttendance(memberId);
        return ResponseEntity.ok().body(memberAttendanceResponse);
    }
}
