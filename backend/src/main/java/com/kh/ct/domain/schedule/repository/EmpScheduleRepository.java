package com.kh.ct.domain.schedule.repository;

import com.kh.ct.domain.schedule.entity.EmpSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmpScheduleRepository extends JpaRepository<EmpSchedule, Long> {
    
    // 스케줄 ID로 배정된 직원 조회 (JOIN FETCH로 N+1 문제 방지)
    @Query("SELECT es FROM EmpSchedule es " +
           "JOIN FETCH es.empId emp " +
           "WHERE es.scheduleId.scheduleId = :scheduleId")
    List<EmpSchedule> findByScheduleId(@Param("scheduleId") Long scheduleId);
    
    // 직원 ID로 배정된 스케줄 조회 (JOIN FETCH로 N+1 문제 방지)
    @Query("SELECT es FROM EmpSchedule es " +
           "JOIN FETCH es.scheduleId s " +
           "WHERE es.empId.empId = :empId")
    List<EmpSchedule> findByEmpId(@Param("empId") String empId);
}
