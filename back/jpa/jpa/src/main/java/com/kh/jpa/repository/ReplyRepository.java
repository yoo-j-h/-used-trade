package com.kh.jpa.repository;

import com.kh.jpa.entity.Reply;
import com.kh.jpa.enums.CommonEnums;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository {

    void save(Reply reply);

    Optional<Reply> findById(Long replyNo);

    List<Reply> findByBoardIdAndStatus(Long boardId, CommonEnums.Status status);
}
