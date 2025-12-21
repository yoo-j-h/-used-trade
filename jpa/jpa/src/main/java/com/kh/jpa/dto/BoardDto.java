package com.kh.jpa.dto;

import com.kh.jpa.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class BoardDto {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private String board_title;
        private String board_content;
        private String user_id;
        private String category;
        private String region;
        private Integer price;
        private String sale_status;
        private String image_url;

        public Board toEntity(String resolvedRegion, String resolvedSaleStatus) {
            return Board.builder()
                    .boardTitle(board_title)
                    .boardContent(board_content)
                    .category(category)
                    .region(resolvedRegion)
                    .price(price)
                    .saleStatus(resolvedSaleStatus)
                    .imageUrl(image_url)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
        private String board_title;
        private String board_content;
        private String category;
        private Integer price;
        private String sale_status;
        private String image_url;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long board_id;
        private String board_title;
        private String board_content;
        private String category;
        private String region;
        private Integer price;
        private String sale_status;
        private String image_url;
        private Integer count;
        private String user_id;
        private String user_name;
        private LocalDateTime create_date;

        public static Response of(
                Long boardId, String boardTitle, String boardContent,
                String category, String region, Integer price, String saleStatus,
                String imageUrl, Integer count,
                String userId, String userName,
                LocalDateTime createDate
        ) {
            return Response.builder()
                    .board_id(boardId)
                    .board_title(boardTitle)
                    .board_content(boardContent)
                    .category(category)
                    .region(region)
                    .price(price)
                    .sale_status(saleStatus)
                    .image_url(imageUrl)
                    .count(count)
                    .user_id(userId)
                    .user_name(userName)
                    .create_date(createDate)
                    .build();
        }

        public static Response ofSimple(
                Long boardId, String boardTitle,
                Integer price, String saleStatus,
                String imageUrl, Integer count,
                String region,
                String userId, LocalDateTime createDate
        ) {
            return Response.builder()
                    .board_id(boardId)
                    .board_title(boardTitle)
                    .price(price)
                    .sale_status(saleStatus)
                    .image_url(imageUrl)
                    .count(count)
                    .region(region)
                    .user_id(userId)
                    .create_date(createDate)
                    .build();
        }
    }
}
