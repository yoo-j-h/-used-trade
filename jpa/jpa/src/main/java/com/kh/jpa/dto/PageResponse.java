package com.kh.jpa.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

//페이징 처리 반환 타입
// 직렬화시 getter가 필수
@Getter
public class PageResponse<T> {
    private List<T> content;
    private Long total_count;
    private int current_page;
    private int page_size;
    private boolean has_next;
    private boolean has_prev;

    public PageResponse(Page<T> page) {
        this.content = page.getContent(); //조회한 데이터 목록
        this.current_page = page.getNumber(); //지금 조회한 페이지
        this.page_size = page.getTotalPages(); //총 페이지 수
        this.total_count = page.getTotalElements(); //총 게시글 수
        this.has_next = page.hasNext(); //다음페이지 여부
        this.has_prev = page.hasPrevious(); //이전페이지 여부
    }
}
