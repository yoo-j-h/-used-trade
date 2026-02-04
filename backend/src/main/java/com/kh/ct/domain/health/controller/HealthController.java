package com.kh.ct.domain.health.controller;

import com.kh.ct.domain.health.dto.HealthDto;
import com.kh.ct.domain.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Validated
public class HealthController {

    private final HealthService healthService;

    /**
     * 건강 정보 텍스트 추출
     * @param file
     * @return
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HealthDto.PhysicalTestResponse> preview(@RequestParam(value = "file", required = true) MultipartFile file) {
        //서비스 호출

        HealthDto.PhysicalTestResponse empPhysicalTestId = healthService.preview(file);

        return ResponseEntity.ok(empPhysicalTestId);
    }

    /**
     * 건강 정보 제출
     * @param empId
     * @param file
     * @param data
     * @return
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> save (@RequestParam("empId") String empId,
                      @RequestPart("file") MultipartFile file,
                      @RequestPart("data") HealthDto.PhysicalTestRequest data) {
        System.out.println(empId);
        System.out.println(file);
        System.out.println(data);
        Long empPhysicalTestId = healthService.save(file, empId, data);

        return ResponseEntity.ok(empPhysicalTestId);
    }


    /**
     * 건강 정보 상세 보기
     * @param empId
     * @return
     */
    @GetMapping("/detail")
    public ResponseEntity<HealthDto.PhysicalTestDetailResponse> getEmpPhysicalById(@RequestParam("empId") String empId) {
        HealthDto.PhysicalTestDetailResponse result = healthService.getEmpPhysicalTestById(empId);
        return ResponseEntity.ok(result);
    }

    /**
     * 개인 건강 정보 제출 이력
     * @param empId
     * @param pageable
     * @return
     */
    @GetMapping("/getPhysicalTest")
    public ResponseEntity<Page<HealthDto.PhysicalTestResponse>> getPhysicalTestByEmpId(@RequestParam("empId") String empId,
                                                                                      @PageableDefault(size = 4, sort = "testDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(healthService.getPhysicalTestByEmpId(empId,pageable));
    }

    /**
     * 관리자 직원 건강 상세 조회
     * @param pageable
     * @return
     */
    @GetMapping("/getAllPhysicalTest")
    public ResponseEntity<Page<HealthDto.AdminEmpHealthRow>> getAllPhysicalTest(@RequestParam(required = false) String empName,
            @PageableDefault(size = 5) Pageable pageable) {
        System.out.println(empName);
        return ResponseEntity.ok(healthService.getAllPhysicalTest(empName,pageable));
    }

}
