package com.kh.ct.domain.attendance.entity;

import com.kh.ct.global.entity.BaseTimeEntity;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.domain.emp.entity.Emp;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    private LocalTime inTime;

    private LocalTime outTime;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.AttendanceStatus attendanceStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emp_id")
    private Emp empId;

    /**
     * 근태 정정 처리 (승인된 정정 신청 내용 반영)
     * @param inTime 정정된 출근 시간
     * @param outTime 정정된 퇴근 시간
     * @param status 정정된 근태 상태
     */
    public void updateAttendance(LocalTime inTime, LocalTime outTime, CommonEnums.AttendanceStatus status) {
        if (inTime != null) {
            this.inTime = inTime;
        }
        if (outTime != null) {
            this.outTime = outTime;
        }
        if (status != null) {
            this.attendanceStatus = status;
        }
    }
}