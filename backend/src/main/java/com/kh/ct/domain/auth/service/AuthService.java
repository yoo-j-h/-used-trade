package com.kh.ct.domain.auth.service;

import com.kh.ct.domain.auth.dto.AuthDto;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmpRepository empRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {

        // 1) 회원 조회 (empId 기준 조회가 안전)
        Emp emp = empRepository.findById(request.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다."));

        // 2) 비밀번호 검증
        if (!passwordEncoder.matches(request.getEmpPwd(), emp.getEmpPwd())) {
            throw new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다.");
        }

        // 3) 토큰 발급
        String empId = emp.getEmpId();
        String role = emp.getRole().name();
        String token = jwtTokenProvider.generateToken(empId, role);

        // 4) 분리형: 토큰만 반환
        return AuthDto.LoginResponse.builder()
                .token(token)
                .build();
    }
    @Transactional(readOnly = true)
    public AuthDto.MeResponse me(String empId) {
        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + empId));

        return AuthDto.MeResponse.builder()
                .empId(emp.getEmpId())
                .empName(emp.getEmpName())
                .role(emp.getRole().name()) // role이 Enum이면 .name()
                .airlineId(emp.getAirlineId() != null ? emp.getAirlineId().getAirlineId() : null)
                .build();
    }
}
