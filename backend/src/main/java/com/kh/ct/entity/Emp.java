package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emp extends BaseTimeEntity {

    @Id
    @Column(length = 50)
    private String empId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Airline airline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String empName;

    @Column(nullable = false, length = 255)
    private String empPwd;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 50) // 승무원/조종사/정비사 등
    private String role;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, length = 50)
    private String job;

    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, length = 1) // N,Y
    private String empStatus;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer leaveCount;

    @Column(length = 30)
    private String applyStatus;

    @Column(nullable = false, length = 50)
    private String empNo;
}
