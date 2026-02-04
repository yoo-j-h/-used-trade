package com.kh.ct.Application.admin_dashboard.repository;

import com.kh.ct.Application.admin_dashboard.dto.Admin_Dashboard_TotalResponseDto;
import com.kh.ct.domain.attendance.entity.Attendance;
import com.kh.ct.domain.attendance.entity.ProtestApply;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.health.entity.EmpHealth;
import com.kh.ct.domain.health.entity.ProgramApply;
import com.kh.ct.domain.schedule.entity.FlySchedule;
import com.kh.ct.domain.schedule.entity.GroundSchedule;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class Admin_Dashboard_RepositoryImpl implements Admin_Dashboard_Repository {

    private final EntityManager em;

   @Override
    public List<Attendance> findByEmp_Leave_Apply(String empId) {

        // JPQL을 사용하여 Emp 엔티티의 empId 필드를 조건으로 조회
        String jpql = "SELECT a FROM Attendance a WHERE a.empId.empId = :empId ORDER BY a.createDate DESC";

        return em.createQuery(jpql, Attendance.class)
                .setParameter("empId", empId)
                .getResultList();
    }

    @Override //현재근무 직원수
    public Long findCurrentWorkingEmployeeCount() {
        // 1. 오늘 날짜 기준 생성 (LocalDate 사용)
        java.time.LocalDate today = java.time.LocalDate.now();

        // 2. JPQL 작성
        // 상태가 PRESENT, LATE, EARLY_LEAVE 중 하나이고,
        // 오늘 날짜이며, outTime이 아직 찍히지 않은(null) 데이터의 개수
        String jpql = "SELECT COUNT(a) FROM Attendance a " +
                "WHERE a.attendanceDate = :today " +
                "AND a.attendanceStatus IN (:status1, :status2) " +
                "AND a.outTime IS NULL";

        return em.createQuery(jpql, Long.class)
                .setParameter("today", today)
                .setParameter("status1", com.kh.ct.global.common.CommonEnums.AttendanceStatus.PRESENT)
                .setParameter("status2", com.kh.ct.global.common.CommonEnums.AttendanceStatus.LATE)
                .getSingleResult();
    }

    @Override
    public Optional<Emp> findByEmp_Emp(String empId) {
        // find() 메서드는 PK를 통해 바로 조회가 가능합니다.
        Emp emp = em.find(Emp.class, empId);
        return Optional.ofNullable(emp);
    }

    @Override
    public Long countTotalEmployees() {
        // Emp 엔티티의 전체 개수를 구하는 JPQL
        String jpql = "SELECT COUNT(e) FROM Emp e";

        return em.createQuery(jpql, Long.class)
                .getSingleResult();
    }

    @Override
    public Long countThisWeekApprovedLeaveEmployees() {
        // 1. 이번 주 월요일 00:00:00 ~ 일요일 23:59:59 계산
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime startOfWeek = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        java.time.LocalDateTime endOfWeek = today.with(java.time.DayOfWeek.SUNDAY).atTime(23, 59, 59);

        // 2. JPQL: 상태가 APPROVED이고, 휴가 기간이 이번 주에 걸쳐 있는 '중복 없는 사원 수'
        // leaveStartDate가 이번 주 종료일 이전이고, leaveEndDate가 이번 주 시작일 이후인 경우 (기간 겹침 확인)
        String jpql = "SELECT COUNT(DISTINCT l.leaveApplyApplicant) FROM LeaveApply l " +
                "WHERE l.leaveApplyStatus = :status " +
                "AND l.leaveStartDate <= :end " +
                "AND l.leaveEndDate >= :start";

        return em.createQuery(jpql, Long.class)
                .setParameter("status", com.kh.ct.global.common.CommonEnums.ApplyStatus.APPROVED)
                .setParameter("start", startOfWeek)
                .setParameter("end", endOfWeek)
                .getSingleResult();
    }

    @Override
    public Long find_Working(String empId) {
        // JPQL: 결석(ABSENT)이 아닌 데이터의 개수만 조회
        String jpql = "SELECT COUNT(a) FROM Attendance a " +
                "WHERE a.empId.empId = :empId " +
                "AND a.attendanceStatus <> :status";

        return em.createQuery(jpql, Long.class)
                .setParameter("empId", empId)
                .setParameter("status", com.kh.ct.global.common.CommonEnums.AttendanceStatus.ABSENT)
                .getSingleResult();
    }

    @Override
    public Long findTotalFlightMinutes(String empId) {
        String jpql = "SELECT SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, f.flyStartTime, f.flyEndTime)) " +
                "FROM FlySchedule f " +
                "JOIN EmpSchedule es ON f.flyScheduleId = es.empScheduleId " +
                "WHERE es.empId.empId = :empId " +
                "AND f.flightStatus = :status";

        // Long.class 대신 Number.class 사용
        Number result = em.createQuery(jpql, Number.class)
                .setParameter("empId", empId)
                .setParameter("status", com.kh.ct.global.common.CommonEnums.flightStatus.ARRIVED)
                .getSingleResult();

        // 결과가 null이면 0, 아니면 longValue로 변환
        return result != null ? result.longValue() : 0L;
    }



    @Override
    public Long findTotalFlightCount(String empId) {
        String jpql = "SELECT COUNT(f) FROM FlySchedule f " +
                "JOIN EmpSchedule es ON f.flyScheduleId = es.empScheduleId " +
                "WHERE es.empId.empId = :empId " +
                "AND f.flightStatus = :status";

        // COUNT는 보통 Long을 반환하지만, 안전하게 Number로 처리 가능
        Number result = em.createQuery(jpql, Number.class)
                .setParameter("empId", empId)
                .setParameter("status", com.kh.ct.global.common.CommonEnums.flightStatus.ARRIVED)
                .getSingleResult();

        return result != null ? result.longValue() : 0L;
    }

    @Override
    public Admin_Dashboard_TotalResponseDto.HealthInfo findAverageHealthMetrics() {
        // 모든 직원의 점수 평균을 구하는 JPQL (null 방지를 위해 COALESCE 사용)
        String jpql = "SELECT " +
                "AVG(h.healthPoint), " +
                "AVG(h.stressPoint), " +
                "AVG(h.fatiguePoint), " +
                "AVG(h.physicalPoint) " +
                "FROM EmpHealth h";

        Object[] result = (Object[]) em.createQuery(jpql)
                .getSingleResult();

        // 결과값이 null일 경우를 대비해 0으로 처리 (Double로 반환되므로 Integer로 변환)
        return Admin_Dashboard_TotalResponseDto.HealthInfo.builder()
                .healthPoint(result[0] != null ? ((Double) result[0]).intValue() : 0)
                .stressPoint(result[1] != null ? ((Double) result[1]).intValue() : 0)
                .fatiguePoint(result[2] != null ? ((Double) result[2]).intValue() : 0)
                .physicalPoint(result[3] != null ? ((Double) result[3]).intValue() : 0)
                .build();
    }

    @Override
    public List<FlySchedule> findFlySchedulesByEmpId(String empId) {
        // [FlySchedule] -> [FlySchedule_ID (MapsId)] -> [AllSchedule (es)] -> [Emp (empId)]
        // 엔티티 구조에 따라 es.empId.empId 경로로 접근합니다.
        String jpql = "SELECT f FROM FlySchedule f " +
                "JOIN EmpSchedule es ON f.flyScheduleId = es.empScheduleId " +
                "WHERE es.empId.empId = :empId " +
                "ORDER BY f.flyStartTime ASC"; // 시간순 정렬 (대시보드용)

        return em.createQuery(jpql, FlySchedule.class)
                .setParameter("empId", empId)
                .getResultList();
    }

    @Override
    public List<GroundSchedule> findGroundSchedulesByEmpId(String empId) {
        return em.createQuery(
                        "select g from GroundSchedule g where g.empId.empId = :empId " +
                                "order by g.scheduleStartDate asc", GroundSchedule.class)
                .setParameter("empId", empId)
                .setMaxResults(3)
                .getResultList();
    }


    @Override
    public List<ProgramApply> findProgramAppliesByEmpId(String empId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        // 🚩 날짜와 상태 필터를 제거하고 empId로만 전체 조회
        String jpql = "SELECT pa FROM ProgramApply pa " +
                "WHERE pa.programApplyApplicant.empId = :empId " +
                "ORDER BY pa.programApplyDate DESC"; // 최신 신청 건부터 보이게 DESC로 변경

        return em.createQuery(jpql, ProgramApply.class)
                .setParameter("empId", empId)
                .getResultList();
    }
    // 휴가 신청 대기 수
    @Override
    public Long countPendingLeaveApplies() {
        return em.createQuery("SELECT COUNT(l) FROM LeaveApply l WHERE l.leaveApplyStatus IN :status", Long.class)
                .setParameter("status", List.of(
                        com.kh.ct.global.common.CommonEnums.ApplyStatus.PENDING,
                        com.kh.ct.global.common.CommonEnums.ApplyStatus.REJECTED
                ))
                .getSingleResult();
    }

    // 건강 프로그램 신청 대기 수 (ProgramApply 테이블)
    @Override
    public Long countPendingProgramApplies() {
        return em.createQuery("SELECT COUNT(p) FROM ProgramApply p WHERE p.programApplyStatus IN :status", Long.class)
                .setParameter("status", List.of(
                        com.kh.ct.global.common.CommonEnums.ApplyStatus.PENDING,
                        com.kh.ct.global.common.CommonEnums.ApplyStatus.REJECTED
                ))
                .getSingleResult();
    }

    // 근태 정정 신청 대기 수 (ProtestApply 테이블)
    @Override
    public Long countPendingProtestApplies() {
        return em.createQuery("SELECT COUNT(pr) FROM ProtestApply pr WHERE pr.protestApplyStatus IN :status", Long.class)
                .setParameter("status", List.of(
                        com.kh.ct.global.common.CommonEnums.ApplyStatus.PENDING,
                        com.kh.ct.global.common.CommonEnums.ApplyStatus.REJECTED
                ))
                .getSingleResult();
    }

    //휴가 신청 총 개수
    @Override
    public Long countTotalLeaveApplies() {
        return em.createQuery("SELECT COUNT(l) FROM LeaveApply l", Long.class).getSingleResult();
    }
    //건강프로그램 신청 총 개수
    @Override
    public Long countTotalProgramApplies() {
        return em.createQuery("SELECT COUNT(p) FROM ProgramApply p", Long.class).getSingleResult();
    }
    //근태정정 신청 총 개수
    @Override
    public Long countTotalProtestApplies() {
        return em.createQuery("SELECT COUNT(pr) FROM ProtestApply pr", Long.class).getSingleResult();
    }

}

/*
@Override
public List<FlySchedule> findFlySchedulesByEmpId(String empId) {
    // 1. 날짜 범위 설정 (오늘 00:00:00 ~ 내일 23:59:59)
    java.time.LocalDateTime startDateTime = java.time.LocalDate.now().atStartOfDay();
    java.time.LocalDateTime endDateTime = java.time.LocalDate.now().plusDays(1).atTime(23, 59, 59);

    // 2. FlySchedule은 LocalDateTime(flyStartTime)을 사용하므로 시간까지 비교
    String jpql = "SELECT f FROM FlySchedule f " +
            "JOIN EmpSchedule es ON f.flyScheduleId = es.empScheduleId " +
            "WHERE es.empId.empId = :empId " +
            "AND f.flyStartTime BETWEEN :start AND :end " + // 🚩 날짜 필터 추가
            "ORDER BY f.flyStartTime ASC";

    return em.createQuery(jpql, FlySchedule.class)
            .setParameter("empId", empId)
            .setParameter("start", startDateTime)
            .setParameter("end", endDateTime)
            .getResultList();
}

@Override
public List<GroundSchedule> findGroundSchedulesByEmpId(String empId) {
    // 1. 오늘과 내일 날짜 계산
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate tomorrow = today.plusDays(1);

    // 2. GroundSchedule은 LocalDate(scheduleStartDate)를 사용하므로 날짜 비교
    return em.createQuery(
            "SELECT g FROM GroundSchedule g " +
            "WHERE g.empId.empId = :empId " +
            "AND g.scheduleStartDate BETWEEN :startDate AND :endDate " + // 🚩 날짜 필터 추가
            "ORDER BY g.scheduleStartDate ASC, g.scheduleStartTime ASC", GroundSchedule.class)
            .setParameter("empId", empId)
            .setParameter("startDate", today)
            .setParameter("endDate", tomorrow)
            .getResultList(); // .setMaxResults(3)는 제거해도 됩니다 (오늘/내일 데이터만 가져오므로)
}

@Override
    public List<ProgramApply> findProgramAppliesByEmpId(String empId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        // 🚩 제공해주신 스타일대로 날짜 범위와 사번을 필터링하는 JPQL 작성
        String jpql = "SELECT pa FROM ProgramApply pa " +
                "WHERE pa.programApplyApplicant.empId = :empId " +
                "AND pa.programApplyDate BETWEEN :start AND :end " +
                "AND pa.programApplyStatus = :status " + // 승인된 것만 보고 싶을 경우 추가 (선택사항)
                "ORDER BY pa.programApplyDate ASC";

        return em.createQuery(jpql, ProgramApply.class)
                .setParameter("empId", empId)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("status", com.kh.ct.global.common.CommonEnums.ApplyStatus.APPROVED) // 예: 승인된 일정만
                .getResultList();
    }
* */