package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    private LocalDateTime attendanceDate;

    private LocalTime inTime;
    private LocalTime outTime;

    @Column(length = 30)
    private String attendanceStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp emp;
}