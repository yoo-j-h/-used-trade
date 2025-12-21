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

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    private static final String UPLOAD_DIR = "uploads" + File.separator;

    @Override
    @Transactional
    public Long createBoard(BoardDto.Create createDto) throws IOException {

        Member member = memberRepository.findById(createDto.getUser_id())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

        String saleStatus = (createDto.getSale_status() == null || createDto.getSale_status().isBlank())
                ? "판매중"
                : createDto.getSale_status();

        String imageUrl = null;
        if (createDto.getFile() != null && !createDto.getFile().isEmpty()) {
            String originName = createDto.getFile().getOriginalFilename();
            String changeName = UUID.randomUUID() + "_" + originName;

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            createDto.getFile().transferTo(new File(UPLOAD_DIR + changeName));
            imageUrl = "/images/" + changeName;
        }

        Board board = createDto.toEntity();
        board.patchUpdate(null, null, null, null, saleStatus);
        board.changeMember(member);
        board.changeImageUrl(imageUrl);

        boardRepository.save(board);
        return board.getBoardId();
    }

    @Override
    @Transactional
    public BoardDto.Response getBoardDetail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.increaseCount();

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
    public BoardDto.Response updateBoard(Long boardId, BoardDto.Update updateDto) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.patchUpdate(
                updateDto.getBoard_title(),
                updateDto.getBoard_content(),
                updateDto.getCategory(),
                updateDto.getPrice(),
                updateDto.getSale_status()
        );

        if (updateDto.getFile() != null && !updateDto.getFile().isEmpty()) {
            String originName = updateDto.getFile().getOriginalFilename();
            String changeName = UUID.randomUUID() + "_" + originName;

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            updateDto.getFile().transferTo(new File(UPLOAD_DIR + changeName));
            board.changeImageUrl("/images/" + changeName);
        }

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
