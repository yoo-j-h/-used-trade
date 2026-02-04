package com.kh.ct.domain.emp.controller;

import com.kh.ct.domain.emp.dto.EmpDto;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.service.EmpNoService;
import com.kh.ct.domain.emp.service.EmpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.kh.ct.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emps")
@RequiredArgsConstructor
@Validated
public class EmpController {

    private final EmpService empService;
    private final EmpNoService empNoService;

    /** 아이디 중복체크 */
    @GetMapping("/checkId")
    public ResponseEntity<EmpDto.IdCheckResponse> checkEmpId(
            @RequestParam @NotBlank(message = "empId는 필수입니다.") String empId
    ) {
        boolean available = empService.isEmpIdAvailable(empId);
        return ResponseEntity.ok(EmpDto.IdCheckResponse.of(available));
    }

    /** ✅ 사번 미리보기: 가입 전에 화면에 보여주기 */
    @GetMapping("/empNo/preview")
    public ResponseEntity<EmpDto.EmpNoPreviewResponse> previewEmpNo() {
        String empNo = empNoService.previewEmpNo();
        return ResponseEntity.ok(EmpDto.EmpNoPreviewResponse.of(empNo));
    }

    /** 회원가입 */
    @PostMapping
    public ResponseEntity<EmpDto.RegisterResponse> register(
            @Valid @RequestBody EmpDto.RegisterRequest request
    ) {
        Emp created = empService.register(request);
        return ResponseEntity.ok(EmpDto.RegisterResponse.from(created));
    }

    /**
     * 직원 상세 정보 조회
     */
    @GetMapping("/{empId}")
    public ResponseEntity<ApiResponse<EmpDto>> getEmpDetail(@PathVariable String empId) {
        EmpDto empDetail = empService.getEmpDetail(empId);
        return ResponseEntity.ok(ApiResponse.success("직원 상세 정보 조회 성공", empDetail));
    }
}
