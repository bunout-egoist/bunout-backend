package dough.member.controller;

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
    public ResponseEntity<MemberInfoResponse> getMemberInfo() {
        final MemberInfoResponse memberInfoResponse = memberService.getMemberInfo();
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping
    public ResponseEntity<MemberInfoResponse> updateMemberInfo(@RequestBody @Valid final MemberInfoRequest memberInfoRequest) {
        final MemberInfoResponse memberInfoResponse = memberService.updateMemberInfo(memberInfoRequest);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @PutMapping("/burnout")
    public ResponseEntity<Void> updateBurnout(@RequestBody @Valid final BurnoutRequest burnoutRequest
    ) {
        memberService.updateBurnout(burnoutRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/fixed")
    public ResponseEntity<Void> updateFixedQuest(@RequestBody @Valid final FixedQuestRequest fixedQuestRequest
    ) {
        memberService.updateFixedQuest(fixedQuestRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/attendance")
    public ResponseEntity<MemberAttendanceResponse> checkAttendance() {
        final MemberAttendanceResponse memberAttendanceResponse = memberService.checkAttendance();
        return ResponseEntity.ok().body(memberAttendanceResponse);
    }
}
