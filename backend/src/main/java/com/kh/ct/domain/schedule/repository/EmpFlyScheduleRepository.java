package com.kh.ct.domain.schedule.repository;

import com.kh.ct.domain.schedule.entity.EmpFlySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmpFlyScheduleRepository extends JpaRepository<EmpFlySchedule, Long> {
    
    // 비행편 ID로 배정된 직원 조회 (JOIN FETCH로 N+1 문제 방지, Department 포함, role과 empName으로 정렬)
    @Query("SELECT efs FROM EmpFlySchedule efs " +
           "JOIN FETCH efs.emp emp " +
           "LEFT JOIN FETCH emp.departmentId dept " +
           "JOIN FETCH efs.flySchedule fs " +
           "WHERE fs.flyScheduleId = :flyScheduleId " +
           "ORDER BY emp.role, emp.empName")
    List<EmpFlySchedule> findByFlyScheduleId(@Param("flyScheduleId") Long flyScheduleId);
    
    // 직원 ID로 배정된 비행편 조회 (JOIN FETCH로 N+1 문제 방지)
    @Query("SELECT efs FROM EmpFlySchedule efs " +
           "JOIN FETCH efs.flySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "WHERE efs.emp.empId = :empId")
    List<EmpFlySchedule> findByEmpId(@Param("empId") String empId);
    
    // 비행편 ID와 직원 ID로 조회
    @Query("SELECT efs FROM EmpFlySchedule efs " +
           "JOIN FETCH efs.emp emp " +
           "JOIN FETCH efs.flySchedule fs " +
           "WHERE efs.flySchedule.flyScheduleId = :flyScheduleId " +
           "AND efs.emp.empId = :empId")
    List<EmpFlySchedule> findByFlyScheduleIdAndEmpId(
            @Param("flyScheduleId") Long flyScheduleId,
            @Param("empId") String empId
    );
}
