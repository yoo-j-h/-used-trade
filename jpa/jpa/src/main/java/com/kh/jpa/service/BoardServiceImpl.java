package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import com.kh.jpa.repository.BoardRepository;
import com.kh.jpa.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public Long createBoard(BoardDto.Create createDto) {

        Member member = memberRepository.findById(createDto.getUser_id())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

        String resolvedRegion = (createDto.getRegion() == null || createDto.getRegion().isBlank())
                ? member.getAddress()
                : createDto.getRegion();

        String resolvedSaleStatus = (createDto.getSale_status() == null || createDto.getSale_status().isBlank())
                ? "판매중"
                : createDto.getSale_status();

        Board board = createDto.toEntity(resolvedRegion, resolvedSaleStatus);
        board.changeMember(member);

        boardRepository.save(board);
        return board.getBoardId();
    }

    @Override
    public BoardDto.Response getBoardDetail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        return BoardDto.Response.of(
                board.getBoardId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                board.getCategory(),
                board.getRegion(),
                board.getPrice(),
                board.getSaleStatus(),
                board.getImageUrl(),
                board.getCount(),
                board.getMember().getUserId(),
                board.getMember().getUserName(),
                board.getCreateDate()
        );
    }

    @Override
    public Page<BoardDto.Response> getBoardList(Pageable pageable) {
        Page<Board> page = boardRepository.findAll(pageable);

        return page.map(board -> BoardDto.Response.ofSimple(
                board.getBoardId(),
                board.getBoardTitle(),
                board.getPrice(),
                board.getSaleStatus(),
                board.getImageUrl(),
                board.getCount(),
                board.getRegion(),
                board.getMember().getUserId(),
                board.getCreateDate()
        ));
    }

    @Override
    @Transactional
    public BoardDto.Response updateBoard(Long boardId, BoardDto.Update updateDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.patchUpdate(
                updateDto.getBoard_title(),
                updateDto.getBoard_content(),
                updateDto.getCategory(),
                updateDto.getPrice(),
                updateDto.getSale_status(),
                updateDto.getImage_url()
        );

        return BoardDto.Response.of(
                board.getBoardId(),
                board.getBoardTitle(),
                board.getBoardContent(),
                board.getCategory(),
                board.getRegion(),
                board.getPrice(),
                board.getSaleStatus(),
                board.getImageUrl(),
                board.getCount(),
                board.getMember().getUserId(),
                board.getMember().getUserName(),
                board.getCreateDate()
        );
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        boardRepository.delete(board);
    }
}
