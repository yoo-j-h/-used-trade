package com.kh.ct.domain.schedule.repository;

import com.kh.ct.domain.schedule.entity.FlySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlyScheduleRepository extends JpaRepository<FlySchedule, Long> {
    
    // 전체 비행편 조회 (날짜 빠른 순서대로)
    @Query("SELECT fs FROM FlySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "ORDER BY fs.flyStartTime ASC")
    List<FlySchedule> findAllByOrderByFlyStartTimeAsc();
    
    // 항공사별 비행편 조회 (날짜 빠른 순서대로)
    @Query("SELECT fs FROM FlySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "WHERE (:airlineId IS NULL OR fs.airlineId = :airlineId) " +
           "ORDER BY fs.flyStartTime ASC")
    List<FlySchedule> findByAirlineId(@Param("airlineId") Long airlineId);
    
    // 날짜 범위로 조회 (날짜 빠른 순서대로)
    @Query("SELECT fs FROM FlySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "WHERE (:airlineId IS NULL OR fs.airlineId = :airlineId) " +
           "AND fs.flyStartTime >= :startDate " +
           "AND fs.flyStartTime < :endDate " +
           "ORDER BY fs.flyStartTime ASC")
    List<FlySchedule> findByDateRange(
        @Param("airlineId") Long airlineId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // 출발지/도착지로 조회 (날짜 빠른 순서대로)
    @Query("SELECT fs FROM FlySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "WHERE (:airlineId IS NULL OR fs.airlineId = :airlineId) " +
           "AND (:departure IS NULL OR fs.departure LIKE CONCAT('%', :departure, '%')) " +
           "AND (:destination IS NULL OR fs.destination LIKE CONCAT('%', :destination, '%')) " +
           "ORDER BY fs.flyStartTime ASC")
    List<FlySchedule> findByDepartureAndDestination(
        @Param("airlineId") Long airlineId,
        @Param("departure") String departure,
        @Param("destination") String destination
    );
    
    // 직원이 배정된 비행편 조회 (날짜 빠른 순서대로) - EmpFlySchedule 사용
    @Query("SELECT DISTINCT fs FROM FlySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "JOIN EmpFlySchedule efs ON efs.flySchedule.flyScheduleId = fs.flyScheduleId " +
           "WHERE efs.emp.empId = :empId " +
           "ORDER BY fs.flyStartTime ASC")
    List<FlySchedule> findByEmpId(@Param("empId") String empId);
    
    // 비행편 ID로 조회
    @Query("SELECT fs FROM FlySchedule fs " +
           "JOIN FETCH fs.schedule s " +
           "WHERE fs.flyScheduleId = :flyScheduleId")
    Optional<FlySchedule> findByFlyScheduleId(@Param("flyScheduleId") Long flyScheduleId);
}
