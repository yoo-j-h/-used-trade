package com.kh.ct.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDto
{
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @NotBlank(message = "사용자 ID는 필수입니다")
        private String empId;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String empPwd; }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private String token;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MeResponse {
        private String empId;
        private String empName;
        private String role;
        private Long airlineId;
    }
}
