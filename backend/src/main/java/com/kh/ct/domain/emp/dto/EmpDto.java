package com.kh.ct.domain.emp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kh.ct.domain.emp.entity.Emp;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpDto {

    private String empId;
    private String empName;
    private String empNo;
    private String role;
    private String job;
    private String phone;
    private String email;
    private String address;
    private String empStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer age;
    private String departmentName;
    private String airlineName;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {

        @JsonProperty("emp_id")
        @NotBlank(message = "아이디(empId)는 필수입니다.")
        @Size(max = 50, message = "아이디는 50자 이하여야 합니다.")
        private String empId;

        @JsonProperty("emp_no")
        @NotBlank(message = "사번(empNo)은 필수입니다.")
        private String empNo;

        @JsonProperty("emp_name")
        @NotBlank(message = "이름(empName)은 필수입니다.")
        @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
        private String empName;

        @JsonProperty("age")
        @NotNull(message = "나이(age)는 필수입니다.")
        @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
        private Integer age;

        @JsonProperty("emp_pwd")
        @NotBlank(message = "비밀번호(empPwd)는 필수입니다.")
        @Size(min = 4, max = 255, message = "비밀번호는 4~255자여야 합니다.")
        private String empPwd;

        @JsonProperty("email")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 150, message = "이메일은 150자 이하여야 합니다.")
        private String email;

        @JsonProperty("phone")
        @Size(max = 30, message = "전화번호는 30자 이하여야 합니다.")
        private String phone;

        @JsonProperty("address")
        @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
        private String address;


        @JsonProperty("profile_image_id")
        private Long profileImageId;
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class RegisterResponse {

        @JsonProperty("emp_id")
        private String empId;

        @JsonProperty("emp_name")
        private String empName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("create_date")
        private LocalDateTime createDate;

        public static RegisterResponse from(Emp emp) {
            return RegisterResponse.builder()
                    .empId(emp.getEmpId())
                    .empName(emp.getEmpName())
                    .email(emp.getEmail())
                    .createDate(emp.getCreateDate())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class IdCheckResponse {

        @JsonProperty("available")
        private boolean available;

        public static IdCheckResponse of(boolean available) {
            return IdCheckResponse.builder()
                    .available(available)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class EmpNoPreviewResponse {
        @JsonProperty("emp_no")
        private String empNo;

        public static EmpNoPreviewResponse of(String empNo) {
            return EmpNoPreviewResponse.builder().empNo(empNo).build();
        }
    }
}
