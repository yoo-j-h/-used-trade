package com.kh.jpa.controller;

import com.kh.jpa.dto.BoardDto;
import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Long> createBoard(@RequestBody BoardDto.Create createBoardDto) {
        Long boardId = boardService.createBoard(createBoardDto);
        return ResponseEntity.ok(boardId);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BoardDto.Response>> getAllBoards(
            @PageableDefault(size = 12, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BoardDto.Response> page = boardService.getBoardList(pageable);
        return ResponseEntity.ok(new PageResponse<>(page));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> updateBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody BoardDto.Update updateBoardDto
    ) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, updateBoardDto));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}
