package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    Long createBoard(BoardDto.Create createDto);
    BoardDto.Response getBoardDetail(Long boardId);
    Page<BoardDto.Response> getBoardList(Pageable pageable);
    BoardDto.Response updateBoard(Long boardId, BoardDto.Update updateDto);
    void deleteBoard(Long boardId);
}
