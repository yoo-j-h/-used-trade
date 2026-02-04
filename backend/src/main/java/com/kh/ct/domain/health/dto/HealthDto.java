package com.kh.ct.domain.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.health.entity.EmpPhysicalTest;
import com.kh.ct.global.entity.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class HealthDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhysicalTestRequest {

        @JsonProperty("emp_id")
        private Emp empId;

        @JsonProperty("test_date")
        private LocalDateTime testDate;

        @JsonProperty("weight")
        private Integer weight;

        @JsonProperty("height")
        private Integer height;

        @JsonProperty("blood_sugar")
        private Integer bloodSugar;

        @JsonProperty("systolic_blood_pressure")
        private Integer systolicBloodPressure;

        @JsonProperty("cholesterol")
        private Integer cholesterol;

        @JsonProperty("diastolic_blood_pressure")
        private Integer diastolicBloodPressure;

        @JsonProperty("heart_rate")
        private Integer heartRate;

        @JsonProperty("bmi")
        private Integer bmi;

        @JsonProperty("body_fat")
        private Integer bodyFat;

        @JsonProperty("file_id")
        private File fileId;

        public EmpPhysicalTest toEntity() {
            return EmpPhysicalTest.builder()
                    .empId(empId)
                    .testDate(testDate)
                    .weight(weight)
                    .height(height)
                    .heartRate(heartRate)
                    .bmi(bmi)
                    .bloodSugar(bloodSugar)
                    .systolicBloodPressure(systolicBloodPressure)
                    .cholesterol(cholesterol)
                    .diastolicBloodPressure(diastolicBloodPressure)
                    .bodyFat(bodyFat)
                    .fileId(fileId)
                    .build();
        }

    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhysicalTestResponse {
        @JsonProperty("test_date")
        private LocalDateTime testDate; // "2026-01-26" 형태 권장
        private Integer height;
        private Integer weight;
        @JsonProperty("blood_sugar")
        private Integer bloodSugar;
        @JsonProperty("systolic_blood_pressure")
        private Integer systolicBloodPressure;
        @JsonProperty("diastolic_blood_pressure")
        private Integer diastolicBloodPressure;
        private Integer cholesterol;
        @JsonProperty("heart_rate")
        private Integer heartRate;
        private Integer bmi;
        @JsonProperty("body_fat")
        private Integer bodyFat;
        @JsonProperty("file_id")
        private Long fileId;

        public static PhysicalTestResponse from (PhysicalTestRequest  physicalTest) {
            return PhysicalTestResponse.builder()
                    .testDate(physicalTest.getTestDate())
                    .height(physicalTest.getHeight())
                    .weight(physicalTest.getWeight())
                    .bloodSugar(physicalTest.getBloodSugar())
                    .systolicBloodPressure(physicalTest.getSystolicBloodPressure())
                    .diastolicBloodPressure(physicalTest.getDiastolicBloodPressure())
                    .cholesterol(physicalTest.getCholesterol())
                    .heartRate(physicalTest.getHeartRate())
                    .bmi(physicalTest.getBmi())
                    .bodyFat(physicalTest.getBodyFat())
                    .build();
        }

        public static PhysicalTestResponse from(EmpPhysicalTest e) {
            return PhysicalTestResponse.builder()
                    .testDate(e.getTestDate())   // Response가 LocalDateTime이면 그대로
                    .height(e.getHeight())
                    .weight(e.getWeight())
                    .bloodSugar(e.getBloodSugar())
                    .systolicBloodPressure(e.getSystolicBloodPressure())
                    .diastolicBloodPressure(e.getDiastolicBloodPressure())
                    .cholesterol(e.getCholesterol())
                    .heartRate(e.getHeartRate())
                    .bmi(e.getBmi())
                    .bodyFat(e.getBodyFat())
                    .fileId(e.getFileId() != null ? e.getFileId().getFileId() : null)
                    .build();
        }


    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhysicalTestDetailResponse {
        @JsonProperty("emp_id")
        private String empId;
        @JsonProperty("emp_name")
        private String empName;
        @JsonProperty("start_date")
        private LocalDateTime startDate;
        @JsonProperty("department_name")
        private String departmentName;
        private String job;
        private String email;
        private String phone;
        private String address;

        @JsonProperty("test_date")
        private LocalDateTime testDate; // "2026-01-26" 형태 권장
        private Integer height;
        private Integer weight;
        @JsonProperty("blood_sugar")
        private Integer bloodSugar;
        @JsonProperty("systolic_blood_pressure")
        private Integer systolicBloodPressure;
        @JsonProperty("diastolic_blood_pressure")
        private Integer diastolicBloodPressure;
        private Integer cholesterol;
        @JsonProperty("heart_rate")
        private Integer heartRate;
        private Integer bmi;
        @JsonProperty("body_fat")
        private Integer bodyFat;

        @JsonProperty("health_point")
        private Integer healthPoint;

        public static PhysicalTestDetailResponse from(EmpPhysicalTest e) {
            return PhysicalTestDetailResponse.builder()
                    .testDate(e.getTestDate())
                    .height(e.getHeight())
                    .weight(e.getWeight())
                    .bloodSugar(e.getBloodSugar())
                    .systolicBloodPressure(e.getSystolicBloodPressure())
                    .diastolicBloodPressure(e.getDiastolicBloodPressure())
                    .cholesterol(e.getCholesterol())
                    .heartRate(e.getHeartRate())
                    .bmi(e.getBmi())
                    .bodyFat(e.getBodyFat())
                    .build();
        }


    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminEmpHealthRow {
        @JsonProperty("emp_id")
        private String empId;
        @JsonProperty("emp_name")
        private String empName;
        @JsonProperty("department_name")
        private String departmentName;  // 부서 엔티티에서 가져오는 값
        private String job;
        @JsonProperty("start_date")
        private LocalDateTime startDate;
        @JsonProperty("test_date")
        private LocalDateTime testDate; // 최근 검진일
        @JsonProperty("health_point")
        private Integer healthPoint;          // 개인건강 점수


    }


}
