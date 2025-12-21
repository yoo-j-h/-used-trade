package com.kh.jpa.controller;

import com.kh.jpa.dto.ReplyDto;
import com.kh.jpa.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/boards/{boardId}/replies")
    public ResponseEntity<Long> createReply(@PathVariable Long boardId,
                                            @RequestBody ReplyDto.Create createDto) {
        Long replyNo = replyService.createReply(boardId, createDto);
        return ResponseEntity.ok(replyNo);
    }

    @GetMapping("/boards/{boardId}/replies")
    public ResponseEntity<List<ReplyDto.Response>> getReplies(@PathVariable Long boardId) {
        return ResponseEntity.ok(replyService.getRepliesByBoard(boardId));
    }

    @PatchMapping("/replies/{replyNo}")
    public ResponseEntity<ReplyDto.Response> updateReply(@PathVariable Long replyNo,
                                                         @RequestBody ReplyDto.Update updateDto) {
        return ResponseEntity.ok(replyService.updateReply(replyNo, updateDto));
    }

    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyNo) {
        replyService.deleteReply(replyNo);
        return ResponseEntity.noContent().build();
    }
}
