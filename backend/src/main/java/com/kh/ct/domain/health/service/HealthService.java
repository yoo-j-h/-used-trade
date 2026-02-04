package com.kh.ct.domain.health.service;

import com.kh.ct.domain.health.dto.HealthDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface HealthService {

    public HealthDto.PhysicalTestResponse preview(MultipartFile file);

    public Long save(MultipartFile pdfFile, String empId, HealthDto.PhysicalTestRequest body);

    public HealthDto.PhysicalTestDetailResponse getEmpPhysicalTestById(String empId);

    public Page<HealthDto.PhysicalTestResponse> getPhysicalTestByEmpId(String empId, Pageable pageable);

    public Page<HealthDto.AdminEmpHealthRow> getAllPhysicalTest(String empName, Pageable  pageable);
}
