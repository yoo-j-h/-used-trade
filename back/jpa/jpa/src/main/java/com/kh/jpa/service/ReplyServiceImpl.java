package com.kh.jpa.service;

import com.kh.jpa.dto.ReplyDto;
import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Reply;
import com.kh.jpa.enums.CommonEnums;
import com.kh.jpa.repository.BoardRepository;
import com.kh.jpa.repository.MemberRepository;
import com.kh.jpa.repository.ReplyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long createReply(Long boardId, ReplyDto.Create createDto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(createDto.getUser_id())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (member.getStatus() == CommonEnums.Status.N) {
            throw new IllegalArgumentException("비활성화된 회원입니다.");
        }

        Reply reply = Reply.builder()
                .replyContent(createDto.getReply_content())
                .build();

        reply.changeBoard(board);
        reply.changeMember(member);

        replyRepository.save(reply);
        return reply.getReplyNo();
    }

    @Override
    public List<ReplyDto.Response> getRepliesByBoard(Long boardId) {
        return replyRepository.findByBoardIdAndStatus(boardId, CommonEnums.Status.Y)
                .stream()
                .map(r -> ReplyDto.Response.of(
                        r.getReplyNo(),
                        r.getReplyContent(),
                        r.getBoard().getBoardId(),
                        r.getMember().getUserId(),
                        r.getMember().getUserName(),
                        r.getCreateDate()
                ))
                .toList();
    }

    @Override
    @Transactional
    public ReplyDto.Response updateReply(Long replyNo, ReplyDto.Update updateDto) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        reply.patchUpdate(updateDto.getReply_content());

        return ReplyDto.Response.of(
                reply.getReplyNo(),
                reply.getReplyContent(),
                reply.getBoard().getBoardId(),
                reply.getMember().getUserId(),
                reply.getMember().getUserName(),
                reply.getCreateDate()
        );
    }

    @Override
    @Transactional
    public void deleteReply(Long replyNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        reply.deactivate(); // 소프트 삭제
    }
}
