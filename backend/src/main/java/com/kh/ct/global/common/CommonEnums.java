package com.kh.ct.global.common;

public class CommonEnums {
    public enum CommonStatus {
        Y,N
    }
    public enum ApplyStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum AttendanceStatus {
        PRESENT,          // 출근
        LATE,             // 지각
        EARLY_LEAVE,      // 조퇴
        HALF_DAY,         // 반차
        VACATION,         // 휴가
        ABSENT,           // 결근
        LEAVE_PENDING,    // 휴가 대기 (신청 후 승인 전)
        LEAVE,            // 휴가 (승인 완료)
        PROTEST_PENDING   // 정정 신청 대기
    }

    public enum Role{
        SUPER_ADMIN, AIRLINE_ADMIN, PILOT, CABIN_CREW
        , MAINTENANCE, GROUND_STAFF
    }

    public enum EmpStatus{
        Y,N,S
    }

    public enum flightStatus{
        DELAYED, CANCELLED, DEPARTED, ARRIVED, ASSIGNING, ASSIGNED
    }

    public enum LeaveType {
        ANNUAL,      // 연차
        HALF_DAY,    // 반차
        SICK,        // 병가
        UNPAID       // 무급
    }
}

