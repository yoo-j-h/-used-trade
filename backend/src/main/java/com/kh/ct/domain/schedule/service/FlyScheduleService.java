package com.kh.ct.domain.schedule.service;

import com.kh.ct.domain.schedule.dto.FlyScheduleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FlyScheduleService {
    
    /**
     * 비행편 목록 조회
     * - 관리자: 항공사별 전체 비행편 조회
     * - 직원: 본인이 배정된 비행편만 조회
     */
    List<FlyScheduleDto.ListResponse> getFlightSchedules(
        Long airlineId,
        String empId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String departure,
        String destination
    );
    
    /**
     * 비행편 상세 조회 (크루 정보 포함)
     */
    FlyScheduleDto getFlightScheduleDetail(Long flyScheduleId, String empId);
}
