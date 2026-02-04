package com.kh.ct.domain.emp.service;

import com.kh.ct.domain.emp.dto.EmpDto;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.entity.File;
import com.kh.ct.global.exception.BusinessException;
import com.kh.ct.global.exception.EmpNoConflictException;
import com.kh.ct.global.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpServiceImpl implements EmpService {

    private final EmpRepository empRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpNoService empNoService;

    @Override
    public boolean isEmpIdAvailable(String empId) {
        if (empId == null || empId.isBlank()) {
            throw new IllegalArgumentException("아이디(empId)는 필수입니다.");
        }
        return !empRepository.existsById(empId);
    }

    @Override
    @Transactional
    public Emp register(EmpDto.RegisterRequest request) {

        // 1) 아이디 중복 체크
        if (empRepository.existsById(request.getEmpId())) {
            // ✅ 회원가입 실패는 400이 자연스럽다(너 핸들러에서 BusinessException을 처리하므로)
            throw new BusinessException(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디(empId)입니다.");
        }

        // 2) 사번 중복(사전 체크) - UX 개선용
        if (empRepository.existsByEmpNo(request.getEmpNo())) {
            String newEmpNo = empNoService.previewEmpNo();
            throw new EmpNoConflictException("사번이 이미 사용되었습니다.", newEmpNo);
        }

        File profileImage = null;
        if (request.getProfileImageId() != null) {
            profileImage = fileRepository.findById(request.getProfileImageId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "프로필 이미지 파일이 존재하지 않습니다. fileId=" + request.getProfileImageId()
                    ));
        }

        Emp emp = Emp.builder()
                .empId(request.getEmpId())
                .empNo(request.getEmpNo())
                .empName(request.getEmpName())
                .age(request.getAge())
                .empPwd(passwordEncoder.encode(request.getEmpPwd()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .profileImage(profileImage)

                // 기본값 (정책)
                .role(CommonEnums.Role.CABIN_CREW)
                .job("STAFF")
                .empStatus(CommonEnums.EmpStatus.Y)

                // 기본값(정책)
                .startDate(LocalDateTime.now())
                .endDate(null)
                .leaveCount(15f)

                // nullable이라면 유지
                .airlineId(null)
                .departmentId(null)
                .build();

        // 5) 저장 (동시성 경합으로 UNIQUE(emp_no) 터질 수 있음)
        try {
            return empRepository.save(emp);
        } catch (DataIntegrityViolationException e) {
            // ✅ 최종 방어선: 유니크 충돌이면 새 사번 내려주고 409 처리
            String newEmpNo = empNoService.previewEmpNo();
            throw new EmpNoConflictException("사번이 이미 사용되었습니다.", newEmpNo);
        }
    }

    @Override
    public EmpDto getEmpDetail(String empId) {
        // JOIN FETCH를 사용하여 Department와 Airline을 한 번에 가져옴 (LAZY 직렬화 문제 방지)
        Emp emp = empRepository.findByIdWithDetails(empId)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다. (empId: " + empId + ")"));
        
        // Department 정보 (이미 JOIN FETCH로 로드됨)
        String departmentName = null;
        if (emp.getDepartmentId() != null) {
            departmentName = emp.getDepartmentId().getDepartmentName();
        }
        
        // Airline 정보 (이미 JOIN FETCH로 로드됨)
        String airlineName = null;
        if (emp.getAirlineId() != null) {
            airlineName = emp.getAirlineId().getAirlineName();
        }
        
        // DTO로 변환 (엔티티를 직접 반환하지 않아 순환 참조 방지)
        return EmpDto.builder()
                .empId(emp.getEmpId())
                .empName(emp.getEmpName())
                .empNo(emp.getEmpNo())
                .role(emp.getRole() != null ? emp.getRole().name() : null)
                .job(emp.getJob())
                .phone(emp.getPhone())
                .email(emp.getEmail())
                .address(emp.getAddress())
                .empStatus(emp.getEmpStatus() != null ? emp.getEmpStatus().name() : null)
                .startDate(emp.getStartDate())
                .endDate(emp.getEndDate())
                .age(emp.getAge())
                .departmentName(departmentName)
                .airlineName(airlineName)
                .build();
    }
}
