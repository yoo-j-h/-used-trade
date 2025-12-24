// src/main/java/com/kh/jpa/dto/ReplyDto.java
package com.kh.jpa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

public class ReplyDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {

        @JsonProperty("reply_writer")
        private String user_id;

        @JsonProperty("reply_content")
        private String reply_content;

        @JsonProperty("parent_id")
        private Long parent_id;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {

        @JsonProperty("reply_content")
        private String reply_content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {

        private Long reply_no;
        private String reply_content;
        private Long board_id;

        private String reply_writer;
        private String reply_writer_name;

        private Long parent_id;

        private LocalDateTime create_date;

        public static Response of(
                Long replyNo,
                String replyContent,
                Long boardId,
                String writerId,
                String writerName,
                Long parentId,
                LocalDateTime createDate
        ) {
            return Response.builder()
                    .reply_no(replyNo)
                    .reply_content(replyContent)
                    .board_id(boardId)
                    .reply_writer(writerId)
                    .reply_writer_name(writerName)
                    .parent_id(parentId)
                    .create_date(createDate)
                    .build();
        }
    }
}
