package com.kh.jpa.repository;

import com.kh.jpa.entity.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

    @PersistenceContext
    private EntityManager em;

    private static final Set<String> ALLOWED_SORTS = Set.of("createDate", "boardId", "count", "price");

    @Override
    public Board save(Board board) {
        em.persist(board);
        return board;
    }

    @Override
    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(em.find(Board.class, id));
    }

    @Override
    public Page<Board> findAll(Pageable pageable) {
        String orderBy = "b.createDate desc";
        Sort sort = pageable.getSort();
        if (sort != null && sort.isSorted()) {
            Sort.Order o = sort.iterator().next();
            String prop = o.getProperty();
            if (ALLOWED_SORTS.contains(prop)) {
                orderBy = "b." + prop + " " + (o.isAscending() ? "asc" : "desc");
            }
        }

        String jpql = "select b from Board b order by " + orderBy;
        List<Board> content = em.createQuery(jpql, Board.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery("select count(b) from Board b", Long.class)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void delete(Board board) {
        Board managed = em.contains(board) ? board : em.find(Board.class, board.getBoardId());
        if (managed != null) em.remove(managed);
    }
}
