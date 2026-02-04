package com.kh.ct.domain.emp.service;

import com.kh.ct.domain.emp.dto.EmpDto;
import com.kh.ct.domain.emp.entity.Emp;

public interface EmpService {
    boolean isEmpIdAvailable(String empId);
    Emp register(EmpDto.RegisterRequest request);
    EmpDto getEmpDetail(String empId);
}
