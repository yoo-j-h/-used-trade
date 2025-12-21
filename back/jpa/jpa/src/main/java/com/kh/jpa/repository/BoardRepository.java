package com.kh.jpa.repository;

import com.kh.jpa.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BoardRepository {
    Board save(Board board);
    Optional<Board> findById(Long id);
    Page<Board> findAll(Pageable pageable);
    void delete(Board board);
}
