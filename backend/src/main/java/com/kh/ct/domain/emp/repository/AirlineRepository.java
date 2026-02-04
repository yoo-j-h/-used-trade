package com.kh.ct.domain.emp.repository;

import com.kh.ct.domain.emp.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AirlineRepository extends JpaRepository<Airline, Long> {

    @Query("SELECT a FROM Airline a ORDER BY a.createDate DESC")
    List<Airline> findAllOrderByCreateDateDesc();

    @Query("SELECT a FROM Airline a WHERE a.airlineName LIKE %:keyword% OR CAST(a.airlineId AS string) LIKE %:keyword% ORDER BY a.createDate DESC")
    List<Airline> searchByKeyword(@Param("keyword") String keyword);

    // 직원 수 계산 (Native Query)
    @Query(value = "SELECT COUNT(*) FROM emp WHERE airline_id = :airlineId", nativeQuery = true)
    Long countEmployeesByAirlineId(@Param("airlineId") Long airlineId);

    @Query(value = "SELECT COUNT(*) FROM emp WHERE airline_id = :airlineId AND emp_status = 'Y'", nativeQuery = true)
    Long countActiveEmployeesByAirlineId(@Param("airlineId") Long airlineId);

    // AirlineApply ID로 Airline 조회
    @Query("SELECT a FROM Airline a WHERE a.airlineApplyId.airlineApplyId = :airlineApplyId")
    java.util.Optional<Airline> findByAirlineApplyId(@Param("airlineApplyId") Long airlineApplyId);
    
    // AirlineApply ID로 Airline 존재 여부 확인
    @Query("SELECT COUNT(a) > 0 FROM Airline a WHERE a.airlineApplyId.airlineApplyId = :airlineApplyId")
    boolean existsByAirlineApplyId(@Param("airlineApplyId") Long airlineApplyId);
}

