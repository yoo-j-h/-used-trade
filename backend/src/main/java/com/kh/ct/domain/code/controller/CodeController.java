package com.kh.ct.domain.code.controller;

import com.kh.ct.domain.code.dto.CodeDetailDto;
import com.kh.ct.domain.code.dto.CodeDto;
import com.kh.ct.domain.code.service.CodeService;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common/codes")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;
    private final EmpRepository empRepository;

    /*
     * 코드 조회
     * - SUPER_ADMIN: 모든 항공사의 코드 조회
     * - AIRLINE_ADMIN: 자신의 항공사 코드만 조회
     * */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CodeDto>>> getCode(Authentication authentication) {
        List<CodeDto> codes;

        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null) {
                // SUPER_ADMIN은 모든 코드 조회
                if (emp.getRole() == CommonEnums.Role.SUPER_ADMIN) {
                    codes = codeService.getCode();
                }
                // AIRLINE_ADMIN은 자신의 항공사 코드만 조회
                else if (emp.getRole() == CommonEnums.Role.AIRLINE_ADMIN && emp.getAirlineId() != null) {
                    Long airlineId = emp.getAirlineId().getAirlineId();
                    codes = codeService.getCodeByAirlineId(airlineId);
                }
                // 기타 권한은 자신의 항공사 코드만 조회 (기존 로직)
                else {
                    Long airlineId = emp.getAirlineId() != null ? emp.getAirlineId().getAirlineId() : null;
                    codes = airlineId != null
                            ? codeService.getCodeByAirlineId(airlineId)
                            : codeService.getCode();
                }
            } else {
                codes = codeService.getCode();
            }
        } else {
            codes = codeService.getCode();
        }

        return ResponseEntity.ok(
                ApiResponse.success("공통코드 조회 성공", codes)
        );
    }
    @GetMapping("/{codeId}/details")
    public ResponseEntity<ApiResponse<List<CodeDetailDto>>> getCodeDetails(@PathVariable Long codeId) {
        return ResponseEntity.ok(
                ApiResponse.success("공통코드 디테일 조회 성공", codeService.getCodeDetails(codeId))
        );
    }

    /*
     * 코드 등록
     * - SUPER_ADMIN: 코드 추가 불가 (readonly)
     * - AIRLINE_ADMIN: 자신의 항공사ID로 자동 설정
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<CodeDto>> createCode(
            @RequestBody CodeDto codeDto,
            Authentication authentication) {

        // SUPER_ADMIN은 코드 추가 불가
        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null && emp.getRole() == CommonEnums.Role.SUPER_ADMIN) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                        .body(ApiResponse.fail("최상위관리자는 코드를 추가할 수 없습니다."));
            }
        }

        Long airlineId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null) {
                // AIRLINE_ADMIN은 자신의 항공사ID로 자동 설정
                if (emp.getRole() == CommonEnums.Role.AIRLINE_ADMIN && emp.getAirlineId() != null) {
                    airlineId = emp.getAirlineId().getAirlineId();
                    // codeDto에 airlineId가 명시적으로 설정되지 않은 경우에만 자동 설정
                    if (codeDto.getAirlineId() == null) {
                        codeDto.setAirlineId(airlineId);
                    }
                }
                // 기타 권한은 기존 로직
                else if (emp.getAirlineId() != null) {
                    airlineId = emp.getAirlineId().getAirlineId();
                    if (codeDto.getAirlineId() == null) {
                        codeDto.setAirlineId(airlineId);
                    }
                }
            }
        }

        CodeDto saved = codeService.createCode(codeDto, airlineId);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.success("공통코드 등록 성공", saved));
    }

    /*
     * 코드 디테일 등록
     * - SUPER_ADMIN: 코드 디테일 추가 불가 (readonly)
     * */
    @PostMapping("/{codeId}/details")
    public ResponseEntity<ApiResponse<CodeDetailDto>> createCodeDetail(
            @PathVariable Long codeId,
            @RequestBody CodeDetailDto codeDetailDto,
            Authentication authentication) {

        // SUPER_ADMIN은 코드 디테일 추가 불가
        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null && emp.getRole() == CommonEnums.Role.SUPER_ADMIN) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                        .body(ApiResponse.fail("최상위관리자는 코드 디테일을 추가할 수 없습니다."));
            }
        }

        CodeDetailDto saved = codeService.createCodeDetail(codeId, codeDetailDto);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.success("공통코드 디테일 등록 성공", saved));
    }

    /*
     * 코드 그룹 삭제
     * - SUPER_ADMIN: 코드 삭제 불가 (readonly)
     * */
    @DeleteMapping("/{codeId}")
    public ResponseEntity<ApiResponse<Void>> deleteCode(
            @PathVariable Long codeId,
            Authentication authentication) {

        // SUPER_ADMIN은 코드 삭제 불가
        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null && emp.getRole() == CommonEnums.Role.SUPER_ADMIN) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                        .body(ApiResponse.fail("최상위관리자는 코드를 삭제할 수 없습니다."));
            }
        }

        codeService.deleteCode(codeId);
        return ResponseEntity.ok(ApiResponse.success("공통코드 그룹 삭제 성공", null));
    }

    /*
     * 코드 디테일 수정
     * - SUPER_ADMIN: 코드 디테일 수정 불가 (readonly)
     * */
    @PutMapping("/{codeId}/details/{codeDetailId}")
    public ResponseEntity<ApiResponse<CodeDetailDto>> updateCodeDetail(
            @PathVariable Long codeId,
            @PathVariable Long codeDetailId,
            @RequestBody CodeDetailDto codeDetailDto,
            Authentication authentication) {

        // SUPER_ADMIN은 코드 디테일 수정 불가
        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null && emp.getRole() == CommonEnums.Role.SUPER_ADMIN) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                        .body(ApiResponse.fail("최상위관리자는 코드 디테일을 수정할 수 없습니다."));
            }
        }

        CodeDetailDto updated = codeService.updateCodeDetail(codeId, codeDetailId, codeDetailDto);
        return ResponseEntity.ok(ApiResponse.success("공통코드 디테일 수정 성공", updated));
    }

    /*
     * 코드 디테일 삭제
     * - SUPER_ADMIN: 코드 디테일 삭제 불가 (readonly)
     * */
    @DeleteMapping("/{codeId}/details/{codeDetailId}")
    public ResponseEntity<ApiResponse<Void>> deleteCodeDetail(
            @PathVariable Long codeId,
            @PathVariable Long codeDetailId,
            Authentication authentication) {

        // SUPER_ADMIN은 코드 디테일 삭제 불가
        if (authentication != null && authentication.isAuthenticated()) {
            String empId = authentication.getName();
            Emp emp = empRepository.findById(empId).orElse(null);

            if (emp != null && emp.getRole() == CommonEnums.Role.SUPER_ADMIN) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                        .body(ApiResponse.fail("최상위관리자는 코드 디테일을 삭제할 수 없습니다."));
            }
        }

        codeService.deleteCodeDetail(codeId, codeDetailId);
        return ResponseEntity.ok(ApiResponse.success("공통코드 디테일 삭제 성공", null));
    }
}
