package com.kh.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ReplyDto {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private String user_id;
        private String reply_content;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
        private String reply_content;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long reply_no;
        private String reply_content;
        private Long board_id;
        private String user_id;
        private String user_name;
        private LocalDateTime create_date;

        public static Response of(Long replyNo, String replyContent,
                                  Long boardId, String userId, String userName,
                                  LocalDateTime createDate) {
            return Response.builder()
                    .reply_no(replyNo)
                    .reply_content(replyContent)
                    .board_id(boardId)
                    .user_id(userId)
                    .user_name(userName)
                    .create_date(createDate)
                    .build();
        }
    }
}
