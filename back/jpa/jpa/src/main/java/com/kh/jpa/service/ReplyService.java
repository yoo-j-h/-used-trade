package com.kh.jpa.service;

import com.kh.jpa.dto.ReplyDto;

import java.util.List;

public interface ReplyService {
    Long createReply(Long boardId, ReplyDto.Create createDto);
    List<ReplyDto.Response> getRepliesByBoard(Long boardId);
    ReplyDto.Response updateReply(Long replyNo, ReplyDto.Update updateDto);
    void deleteReply(Long replyNo);
}
