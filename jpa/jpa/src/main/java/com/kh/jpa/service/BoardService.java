package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface BoardService {
    Long createBoard(BoardDto.Create createDto) throws IOException;
    BoardDto.Response getBoardDetail(Long boardId);
    Page<BoardDto.Response> getBoardList(Pageable pageable);
    BoardDto.Response updateBoard(Long boardId, BoardDto.Update updateDto) throws IOException;
    void deleteBoard(Long boardId);
}
