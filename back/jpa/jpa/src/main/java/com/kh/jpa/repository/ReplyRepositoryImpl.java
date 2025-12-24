package com.kh.jpa.repository;

import com.kh.jpa.entity.Reply;
import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReplyRepositoryImpl implements ReplyRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Reply reply) {
        em.persist(reply);
    }

    @Override
    public Optional<Reply> findById(Long replyNo) {
        return Optional.ofNullable(em.find(Reply.class, replyNo));
    }

    @Override
    public List<Reply> findByBoardIdAndStatus(Long boardId, CommonEnums.Status status) {

        String jpql = """
            select r
            from Reply r
            where r.board.boardId = :boardId
              and r.status = :status
            order by r.createDate asc
        """;

        return em.createQuery(jpql, Reply.class)
                .setParameter("boardId", boardId)
                .setParameter("status", status)
                .getResultList();
    }
}
