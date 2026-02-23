package com.kh.ct.domain.schedule.controller;

import com.kh.ct.domain.schedule.dto.FlyScheduleDto;
import com.kh.ct.domain.schedule.service.FlightSyncService;
import com.kh.ct.domain.schedule.service.FlyScheduleService;
import com.kh.ct.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flight-schedules")
@RequiredArgsConstructor
@Validated
public class FlyScheduleController {
    
    private final FlyScheduleService flyScheduleService;
    private final FlightSyncService flightSyncService;

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
        List<FlyScheduleDto.ListResponse> schedules = flyScheduleService.getFlightSchedulesWithAuth(
                authentication, airlineId, startDate, endDate, departure, destination
        );
        return ResponseEntity.ok(ApiResponse.success("비행편 목록 조회 성공", schedules));
    }

    /**
     * 외부 API 데이터 동기화
     */
    @GetMapping("/sync")
    public ResponseEntity<ApiResponse<String>> syncExternalData() {
        flightSyncService.syncApiData();
        return ResponseEntity.ok(ApiResponse.success("비행 스케줄 동기화 요청 성공", "데이터 처리가 시작되었습니다."));
    }
    
    /**
     * 비행편 상세 조회 (크루 정보 포함)
     */
    @GetMapping("/{flyScheduleId}")
    public ResponseEntity<ApiResponse<FlyScheduleDto>> getFlightScheduleDetail(
            @PathVariable Long flyScheduleId,
            Authentication authentication
    ) {
        FlyScheduleDto schedule = flyScheduleService.getFlightScheduleDetailWithAuth(authentication, flyScheduleId);
        return ResponseEntity.ok(ApiResponse.success("비행편 상세 조회 성공", schedule));
    }
    
    /**
     * 승무원 추가 (관리자만 가능)
     */
    @PostMapping("/{flyScheduleId}/crew")
    public ResponseEntity<ApiResponse<Void>> addCrewMember(
            @PathVariable Long flyScheduleId,
            @Valid @RequestBody FlyScheduleDto.AddCrewMemberRequest request,
            Authentication authentication
    ) {
        flyScheduleService.addCrewMemberWithAuth(authentication, flyScheduleId, request.getEmpId());
        return ResponseEntity.ok(ApiResponse.success("승무원 추가 성공", null));
    }
    
    /**
     * 승무원 삭제 (관리자만 가능)
     */
    @DeleteMapping("/{flyScheduleId}/crew/{empId}")
    public ResponseEntity<ApiResponse<Void>> removeCrewMember(
            @PathVariable Long flyScheduleId,
            @PathVariable String empId,
            Authentication authentication
    ) {
        flyScheduleService.removeCrewMemberWithAuth(authentication, flyScheduleId, empId);
        return ResponseEntity.ok(ApiResponse.success("승무원 삭제 성공", null));
    }
}
