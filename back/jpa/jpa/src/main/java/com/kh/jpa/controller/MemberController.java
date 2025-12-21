package com.kh.jpa.controller;

import com.kh.jpa.dto.MemberDto;
import com.kh.jpa.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    //회원등록 api
    @PostMapping
    public ResponseEntity<String> addMember(@RequestBody MemberDto.Create createMemberDto) {
        String userId = memberService.createMember(createMemberDto);
        return ResponseEntity.ok(userId);
    }

    //회원 전체조회 (status=Y만)
    @GetMapping
    public ResponseEntity<List<MemberDto.Response>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    //id로 회원조회 (status=Y만)
    @GetMapping("/{userId}")
    public ResponseEntity<MemberDto.Response> getMember(@PathVariable String userId) {
        return ResponseEntity.ok(memberService.getMemberByUserId(userId));
    }

    //회원수정
    @PutMapping("/{userId}")
    public ResponseEntity<MemberDto.Response> updateMember(
            @PathVariable String userId,
            @RequestBody MemberDto.Update updateMemberDto
    ) {
        MemberDto.Response response = memberService.updateMember(userId, updateMemberDto);
        return ResponseEntity.ok(response);
    }

    //회원삭제 (소프트 삭제: status=N)
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteMember(@PathVariable String userId) {
        memberService.deleteMember(userId);
        return ResponseEntity.ok("ok");
    }

    //이름으로 회원 검색 (status=Y만)
    @GetMapping("/search")
    public ResponseEntity<List<MemberDto.Response>> searchMemberByName(@RequestParam String keyword) {
        return ResponseEntity.ok(memberService.getMembersByName(keyword));
    }
}
