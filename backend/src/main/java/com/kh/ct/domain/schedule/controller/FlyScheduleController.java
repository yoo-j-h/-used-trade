package com.kh.ct.domain.schedule.controller;

import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.domain.schedule.dto.FlyScheduleDto;
import com.kh.ct.domain.schedule.service.FlyScheduleService;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flight-schedules")
@RequiredArgsConstructor
public class FlyScheduleController {
    
    private final FlyScheduleService flyScheduleService;
    private final EmpRepository empRepository;
    
    /**
     * 비행편 목록 조회
     * - 관리자: 항공사별 전체 비행편 조회
     * - 직원: 본인이 배정된 비행편만 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FlyScheduleDto.ListResponse>>> getFlightSchedules(
            Authentication authentication,
            @RequestParam(required = false) Long airlineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination
    ) {
        String empId = null;
        Long finalAirlineId = airlineId;
        
        if (authentication != null && authentication.isAuthenticated()) {
            String authEmpId = authentication.getName();
            Emp emp = empRepository.findById(authEmpId).orElse(null);
            
            if (emp != null) {
                // 관리자가 아니면 본인 배정 비행편만 조회
                if (emp.getRole() != CommonEnums.Role.AIRLINE_ADMIN && 
                    emp.getRole() != CommonEnums.Role.SUPER_ADMIN) {
                    empId = authEmpId;
                } else {
                    // 관리자인 경우 자신의 항공사 ID 사용
                    if (finalAirlineId == null && emp.getAirlineId() != null) {
                        finalAirlineId = emp.getAirlineId().getAirlineId();
                    }
                }
            }
        }
        
        List<FlyScheduleDto.ListResponse> schedules = flyScheduleService.getFlightSchedules(
                finalAirlineId,
                empId,
                startDate,
                endDate,
                departure,
                destination
        );
        
        return ResponseEntity.ok(ApiResponse.success("비행편 목록 조회 성공", schedules));
    }
    
    /**
     * 비행편 상세 조회 (크루 정보 포함)
     */
    @GetMapping("/{flyScheduleId}")
    public ResponseEntity<ApiResponse<FlyScheduleDto>> getFlightScheduleDetail(
            @PathVariable Long flyScheduleId,
            Authentication authentication
    ) {
        String empId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            empId = authentication.getName();
        }
        
        FlyScheduleDto schedule = flyScheduleService.getFlightScheduleDetail(flyScheduleId, empId);
        return ResponseEntity.ok(ApiResponse.success("비행편 상세 조회 성공", schedule));
    }
}
