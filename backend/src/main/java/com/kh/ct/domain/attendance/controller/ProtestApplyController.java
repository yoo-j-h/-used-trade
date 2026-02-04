package com.kh.ct.domain.attendance.controller;

import com.kh.ct.domain.attendance.dto.ProtestDto;
import com.kh.ct.domain.attendance.service.ProtestApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 근태 정정 신청 REST API Controller
 */
@RestController
@RequestMapping("/api/attendance/protest")
@RequiredArgsConstructor
@Slf4j
public class ProtestApplyController {

    private final ProtestApplyService protestApplyService;

    /**
     * 근태 정정 신청
     * 
     * @param empId 직원 ID
     * @param attendanceId 정정 대상 근태 ID
     * @param protestRequestInTime 정정 요청 출근 시간
     * @param protestRequestOutTime 정정 요청 퇴근 시간
     * @param protestReason 정정 사유
     * @param files 증빙 파일 목록
     * @return 정정 신청 응답
     */
    @PostMapping
    public ResponseEntity<ProtestDto.ListResponse> applyProtest(
            @RequestParam String empId,
            @RequestParam Long attendanceId,
            @RequestParam(required = false) String protestRequestInTime,
            @RequestParam(required = false) String protestRequestOutTime,
            @RequestParam String protestReason,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        log.info("POST /api/attendance/protest - empId: {}, attendanceId: {}", empId, attendanceId);

        try {
            ProtestDto.ApplyRequest request = ProtestDto.ApplyRequest.builder()
                    .attendanceId(attendanceId)
                    .protestRequestInTime(protestRequestInTime)
                    .protestRequestOutTime(protestRequestOutTime)
                    .protestReason(protestReason)
                    .build();

            ProtestDto.ListResponse response = protestApplyService.applyProtest(empId, request, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("근태 정정 신청 실패", e);
            throw e;
        }
    }

    /**
     * 내 정정 신청 목록 조회
     * 
     * @param empId 직원 ID
     * @return 정정 신청 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<ProtestDto.ListResponse>> getMyProtestList(
            @RequestParam String empId) {
        
        log.info("GET /api/attendance/protest/my - empId: {}", empId);

        try {
            List<ProtestDto.ListResponse> response = protestApplyService.getMyProtestList(empId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("정정 신청 목록 조회 실패", e);
            throw e;
        }
    }

    /**
     * 정정 신청 상세 조회
     * 
     * @param id 정정 신청 ID
     * @return 정정 신청 상세
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProtestDto.DetailResponse> getProtestDetail(
            @PathVariable Long id) {
        
        log.info("GET /api/attendance/protest/{} - id: {}", id, id);

        try {
            ProtestDto.DetailResponse response = protestApplyService.getProtestDetail(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("정정 신청 상세 조회 실패", e);
            throw e;
        }
    }

    /**
     * 정정 승인/반려
     * 
     * @param id 정정 신청 ID
     * @param approverId 승인자 ID
     * @param request 승인/반려 요청
     * @return 정정 신청 응답
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ProtestDto.ListResponse> approveProtest(
            @PathVariable Long id,
            @RequestParam String approverId,
            @RequestBody ProtestDto.ApproveRequest request) {
        
        log.info("PUT /api/attendance/protest/{}/approve - id: {}, approverId: {}, approved: {}", 
                id, id, approverId, request.getApproved());

        try {
            ProtestDto.ListResponse response = protestApplyService.approveProtest(id, approverId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("정정 승인/반려 실패", e);
            throw e;
        }
    }
}
