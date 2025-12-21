package com.kh.jpa.dto;

import com.kh.jpa.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

public class MemberDto {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        private String user_id;
        private String user_pwd;
        private String user_name;
        private String email;
        private String phone;
        private String address;

        public Member toEntity() {
            return Member.builder()
                    .userId(user_id)
                    .userPwd(user_pwd)
                    .userName(user_name)
                    .email(email)
                    .phone(phone)
                    .address(address)
                    .build();
        }
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {
        private String user_name;
        private String email;
        private String phone;
        private String address;
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String user_id;
        private String user_name;
        private String email;
        private String phone;
        private String address;
        private LocalDateTime create_date;
        private LocalDateTime modify_date;

        public static Response of(
                String user_id,
                String user_name,
                String email,
                String phone,
                String address,
                LocalDateTime create_date,
                LocalDateTime modify_date
        ) {
            return Response.builder()
                    .user_id(user_id)
                    .user_name(user_name)
                    .email(email)
                    .phone(phone)
                    .address(address)
                    .create_date(create_date)
                    .modify_date(modify_date)
                    .build();
        }
    }
}
