package dough.member.controller;

import dough.login.domain.Auth;
import dough.login.domain.Accessor;
import dough.member.dto.request.BurnoutRequest;
import dough.member.dto.request.FixedQuestRequest;
import dough.member.dto.request.MemberInfoRequest;
import dough.member.dto.response.MemberAttendanceResponse;
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

    @GetMapping
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@Auth final Accessor accessor) {
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo(accessor.getMemberId());
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping
    public ResponseEntity<MemberInfoResponse> updateMemberInfo(
            @Auth final Accessor accessor,
            @RequestBody @Valid final MemberInfoRequest memberInfoRequest
    ) {
        final MemberInfoResponse memberInfoResponse = memberService.updateMemberInfo(accessor.getMemberId(), memberInfoRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping("/burnout")
    public ResponseEntity<Void> updateBurnout(
            @Auth final Accessor accessor,
            @RequestBody @Valid final BurnoutRequest burnoutRequest
    ) {
        memberService.updateBurnout(accessor.getMemberId(), burnoutRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/fixed")
    public ResponseEntity<Void> updateFixedQuest(
            @Auth final Accessor accessor,
            @RequestBody @Valid final FixedQuestRequest fixedQuestRequest
    ) {
        memberService.updateFixedQuest(accessor.getMemberId(), fixedQuestRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/attendance")
    public ResponseEntity<MemberAttendanceResponse> checkAttendance(@Auth final Accessor accessor) {
        final MemberAttendanceResponse memberAttendanceResponse = memberService.checkAttendance(accessor.getMemberId());
        return ResponseEntity.ok().body(memberAttendanceResponse);
    }
}
