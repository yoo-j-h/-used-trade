package com.kh.ct.domain.attendance.service;

import com.kh.ct.domain.attendance.dto.LeaveDto;
import com.kh.ct.domain.attendance.entity.Attendance;
import com.kh.ct.domain.attendance.entity.LeaveApply;
import com.kh.ct.domain.attendance.repository.AttendanceRepository;
import com.kh.ct.domain.attendance.repository.LeaveApplyRepository;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.global.common.CommonEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 휴가 신청 Service 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveApplyServiceImpl implements LeaveApplyService {

    private final LeaveApplyRepository leaveApplyRepository;
    private final EmpRepository empRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * 휴가 신청
     */
    @Override
    @Transactional
    public LeaveDto.ListResponse applyLeave(String empId, LeaveDto.ApplyRequest request) {
        log.info("휴가 신청 시작 - empId: {}, leaveType: {}", empId, request.getLeaveType());

        // 1. 직원 조회
        Emp applicant = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다"));

        // 2. 중복 날짜 확인
        if (leaveApplyRepository.existsOverlappingLeave(empId, request.getStartDate(), request.getEndDate())) {
            throw new RuntimeException("이미 해당 기간에 신청한 휴가가 있습니다");
        }

        // 3. 휴가 일수 계산
        Float leaveDays = calculateLeaveDays(request.getLeaveType(), request.getStartDate(), request.getEndDate());

        // 4. 잔여 휴가 확인
        if (applicant.getLeaveCount() == null || applicant.getLeaveCount() < leaveDays) {
            throw new RuntimeException("잔여 휴가가 부족합니다");
        }

        // 5. 휴가 신청 코드 생성
        String leaveCode = generateLeaveCode();

        // 6. 엔티티 생성 및 저장
        LeaveApply leaveApply = LeaveApply.builder()
                .leaveApplyCode(leaveCode)
                .leaveApplyApplicant(applicant)
                .leaveType(CommonEnums.LeaveType.valueOf(request.getLeaveType()))
                .leaveStartDate(request.getStartDate())
                .leaveEndDate(request.getEndDate())
                .leaveDays(leaveDays)
                .leaveApplyReason(request.getReason())
                .leaveApplyStatus(CommonEnums.ApplyStatus.PENDING)
                .build();

        leaveApplyRepository.save(leaveApply);

        // Attendance 자동 생성 (휴가 대기 상태)
        createAttendanceForLeave(leaveApply);

        log.info("휴가 신청 완료 - empId: {}, leaveCode: {}, leaveDays: {}", empId, leaveCode, leaveDays);

        return convertToListResponse(leaveApply);
    }

    /**
     * 내 휴가 신청 내역 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<LeaveDto.ListResponse> getMyLeaveList(String empId) {
        log.info("휴가 내역 조회 - empId: {}", empId);

        List<LeaveApply> leaveList = leaveApplyRepository
                .findByLeaveApplyApplicant_EmpIdOrderByCreateDateDesc(empId);

        return leaveList.stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 잔여 휴가 조회
     */
    @Override
    @Transactional(readOnly = true)
    public LeaveDto.RemainingLeaveResponse getRemainingLeave(String empId) {
        log.info("잔여 휴가 조회 - empId: {}", empId);

        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다"));

        int currentYear = LocalDateTime.now().getYear();
        Float usedLeave = leaveApplyRepository.calculateUsedLeaveByYear(empId, currentYear);
        Float remainingLeave = emp.getLeaveCount() != null ? emp.getLeaveCount() : 0.0f;
        Float totalLeave = remainingLeave + usedLeave;

        int usagePercentage = totalLeave > 0 ? (int) ((usedLeave / totalLeave) * 100) : 0;

        return LeaveDto.RemainingLeaveResponse.builder()
                .totalLeave(totalLeave)
                .usedLeave(usedLeave)
                .remainingLeave(remainingLeave)
                .usagePercentage(usagePercentage)
                .build();
    }

    /**
     * 휴가 승인/반려
     */
    @Override
    @Transactional
    public LeaveDto.ListResponse approveLeave(Long leaveApplyId, String approverId, LeaveDto.ApproveRequest request) {
        log.info("휴가 승인/반려 - leaveApplyId: {}, approverId: {}, approved: {}", 
                leaveApplyId, approverId, request.getApproved());

        // 1. 휴가 신청 조회
        LeaveApply leaveApply = leaveApplyRepository.findById(leaveApplyId)
                .orElseThrow(() -> new RuntimeException("휴가 신청을 찾을 수 없습니다"));

        // 2. 승인자 조회
        Emp approver = empRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("승인자를 찾을 수 없습니다"));

        // 3. 승인/반려 처리
        if (request.getApproved()) {
            // 승인
            leaveApply = LeaveApply.builder()
                    .leaveApplyId(leaveApply.getLeaveApplyId())
                    .leaveApplyCode(leaveApply.getLeaveApplyCode())
                    .leaveApplyApplicant(leaveApply.getLeaveApplyApplicant())
                    .leaveType(leaveApply.getLeaveType())
                    .leaveStartDate(leaveApply.getLeaveStartDate())
                    .leaveEndDate(leaveApply.getLeaveEndDate())
                    .leaveDays(leaveApply.getLeaveDays())
                    .leaveApplyReason(leaveApply.getLeaveApplyReason())
                    .leaveApplyApprover(approver)
                    .leaveApplyStatus(CommonEnums.ApplyStatus.APPROVED)
                    .build();

            log.info("휴가 승인 완료 - leaveApplyId: {}", leaveApplyId);
        } else {
            // 반려
            leaveApply = LeaveApply.builder()
                    .leaveApplyId(leaveApply.getLeaveApplyId())
                    .leaveApplyCode(leaveApply.getLeaveApplyCode())
                    .leaveApplyApplicant(leaveApply.getLeaveApplyApplicant())
                    .leaveType(leaveApply.getLeaveType())
                    .leaveStartDate(leaveApply.getLeaveStartDate())
                    .leaveEndDate(leaveApply.getLeaveEndDate())
                    .leaveDays(leaveApply.getLeaveDays())
                    .leaveApplyReason(leaveApply.getLeaveApplyReason())
                    .leaveApplyApprover(approver)
                    .leaveApplyStatus(CommonEnums.ApplyStatus.REJECTED)
                    .leaveApplyCancelReason(request.getCancelReason())
                    .build();

            log.info("휴가 반려 완료 - leaveApplyId: {}, reason: {}", leaveApplyId, request.getCancelReason());
        }

        leaveApplyRepository.save(leaveApply);

        // Attendance 상태 업데이트 또는 삭제
        if (request.getApproved()) {
            updateAttendanceForApproval(leaveApply);
        } else {
            deleteAttendanceForRejection(leaveApply);
        }

        return convertToListResponse(leaveApply);
    }

    /**
     * 휴가 일수 계산
     */
    private Float calculateLeaveDays(String leaveType, LocalDateTime startDate, LocalDateTime endDate) {
        if ("HALF_DAY".equals(leaveType)) {
            return 0.5f;
        }

        long days = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        return (float) days;
    }

    /**
     * 휴가 신청 코드 생성
     */
    private String generateLeaveCode() {
        return "LV" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * Entity -> DTO 변환
     */
    private LeaveDto.ListResponse convertToListResponse(LeaveApply entity) {
        return LeaveDto.ListResponse.builder()
                .leaveApplyId(entity.getLeaveApplyId())
                .leaveApplyCode(entity.getLeaveApplyCode())
                .leaveType(entity.getLeaveType().name())
                .startDate(entity.getLeaveStartDate())
                .endDate(entity.getLeaveEndDate())
                .leaveDays(entity.getLeaveDays())
                .reason(entity.getLeaveApplyReason())
                .status(entity.getLeaveApplyStatus().name())
                .applicantName(entity.getLeaveApplyApplicant().getEmpName())
                .departmentName(entity.getLeaveApplyApplicant().getDepartmentId() != null ?
                        entity.getLeaveApplyApplicant().getDepartmentId().getDepartmentName() : null)
                .approverName(entity.getLeaveApplyApprover() != null ?
                        entity.getLeaveApplyApprover().getEmpName() : null)
                .createdDate(entity.getCreateDate())
                .build();
    }

    /**
     * 휴가 신청 시 Attendance 자동 생성
     */
    private void createAttendanceForLeave(LeaveApply leaveApply) {
        LocalDate startDate = leaveApply.getLeaveStartDate().toLocalDate();
        LocalDate endDate = leaveApply.getLeaveEndDate().toLocalDate();
        
        log.info("Attendance 자동 생성 시작 - empId: {}, 기간: {} ~ {}", 
                leaveApply.getLeaveApplyApplicant().getEmpId(), startDate, endDate);
        
        // 날짜 범위 반복
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 이미 존재하는 근태 기록이 있는지 확인
            Optional<Attendance> existing = attendanceRepository
                .findByEmpId_EmpIdAndAttendanceDate(
                    leaveApply.getLeaveApplyApplicant().getEmpId(), 
                    date
                );
            
            if (existing.isEmpty()) {
                Attendance attendance = Attendance.builder()
                    .empId(leaveApply.getLeaveApplyApplicant())
                    .attendanceDate(date)
                    .attendanceStatus(CommonEnums.AttendanceStatus.LEAVE_PENDING)
                    .build();
                
                attendanceRepository.save(attendance);
                log.debug("Attendance 생성 - date: {}, status: LEAVE_PENDING", date);
            } else {
                log.debug("Attendance 이미 존재 - date: {}", date);
            }
        }
        
        log.info("Attendance 자동 생성 완료");
    }

    /**
     * 휴가 승인 시 Attendance 상태 업데이트 또는 생성
     */
    private void updateAttendanceForApproval(LeaveApply leaveApply) {
        LocalDate startDate = leaveApply.getLeaveStartDate().toLocalDate();
        LocalDate endDate = leaveApply.getLeaveEndDate().toLocalDate();
        
        log.info("Attendance 상태 업데이트 시작 - empId: {}, 기간: {} ~ {}", 
                leaveApply.getLeaveApplyApplicant().getEmpId(), startDate, endDate);
        
        int updatedCount = 0;
        int createdCount = 0;
        
        // 날짜 범위 반복
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Optional<Attendance> existingOpt = attendanceRepository
                .findByEmpId_EmpIdAndAttendanceDate(
                    leaveApply.getLeaveApplyApplicant().getEmpId(),
                    date
                );
            
            if (existingOpt.isPresent()) {
                // 기존 레코드가 있으면 업데이트
                Attendance existing = existingOpt.get();
                if (existing.getAttendanceStatus() == CommonEnums.AttendanceStatus.LEAVE_PENDING) {
                    Attendance updated = Attendance.builder()
                        .attendanceId(existing.getAttendanceId())
                        .empId(existing.getEmpId())
                        .attendanceDate(existing.getAttendanceDate())
                        .attendanceStatus(CommonEnums.AttendanceStatus.LEAVE)
                        .inTime(existing.getInTime())
                        .outTime(existing.getOutTime())
                        .build();
                    
                    attendanceRepository.save(updated);
                    updatedCount++;
                    log.debug("Attendance 상태 변경 - date: {}, LEAVE_PENDING -> LEAVE", date);
                }
            } else {
                // 레코드가 없으면 새로 생성
                Attendance newAttendance = Attendance.builder()
                    .empId(leaveApply.getLeaveApplyApplicant())
                    .attendanceDate(date)
                    .attendanceStatus(CommonEnums.AttendanceStatus.LEAVE)
                    .build();
                
                attendanceRepository.save(newAttendance);
                createdCount++;
                log.debug("Attendance 생성 - date: {}, status: LEAVE", date);
            }
        }
        
        log.info("Attendance 처리 완료 - 업데이트: {}건, 생성: {}건", updatedCount, createdCount);
    }

    /**
     * 휴가 반려 시 Attendance 삭제
     */
    private void deleteAttendanceForRejection(LeaveApply leaveApply) {
        LocalDate startDate = leaveApply.getLeaveStartDate().toLocalDate();
        LocalDate endDate = leaveApply.getLeaveEndDate().toLocalDate();
        
        log.info("Attendance 삭제 시작 - empId: {}, 기간: {} ~ {}", 
                leaveApply.getLeaveApplyApplicant().getEmpId(), startDate, endDate);
        
        List<Attendance> attendances = attendanceRepository
            .findByEmpId_EmpIdAndAttendanceDateBetween(
                leaveApply.getLeaveApplyApplicant().getEmpId(),
                startDate,
                endDate
            );
        
        // LEAVE_PENDING 상태인 것만 삭제
        int deletedCount = 0;
        for (Attendance attendance : attendances) {
            if (attendance.getAttendanceStatus() == CommonEnums.AttendanceStatus.LEAVE_PENDING) {
                attendanceRepository.delete(attendance);
                deletedCount++;
                log.debug("Attendance 삭제 - date: {}", attendance.getAttendanceDate());
            }
        }
        
        log.info("Attendance 삭제 완료 - {} 건", deletedCount);
    }

    /**
     * 관리자용 전체 휴가 신청 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<LeaveDto.ListResponse> getAllLeaveApplications() {
        log.info("관리자용 전체 휴가 신청 목록 조회");

        List<LeaveApply> leaveList = leaveApplyRepository.findAllWithDetails();

        return leaveList.stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 관리자용 상태별 휴가 신청 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<LeaveDto.ListResponse> getLeaveApplicationsByStatus(String status) {
        log.info("관리자용 상태별 휴가 신청 목록 조회 - status: {}", status);

        CommonEnums.ApplyStatus applyStatus = CommonEnums.ApplyStatus.valueOf(status.toUpperCase());
        List<LeaveApply> leaveList = leaveApplyRepository.findAllByStatusWithDetails(applyStatus);

        return leaveList.stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
    }
}
