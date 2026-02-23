package com.kh.ct.domain.auth.service;

import com.kh.ct.domain.auth.dto.AuthDto;
import com.kh.ct.domain.auth.entity.RefreshToken;
import com.kh.ct.domain.auth.repository.RefreshTokenRepository;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.global.security.CookieUtil;
import com.kh.ct.global.security.JwtTokenProvider;
import com.kh.ct.global.security.RefreshTokenGenerator;
import com.kh.ct.global.security.RefreshTokenHasher;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmpRepository empRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 정책값(원하면 application.yml로 빼세요)
    private static final long ACCESS_EXPIRES_IN = 900L;                 // 15분(초)
    private static final long REFRESH_DAYS = 14;                        // 14일
    private static final long REFRESH_MAX_AGE = 14 * 24 * 60 * 60L;     // 14일(초)

    // 로컬/운영 환경에 맞춰 조정
    private static final boolean COOKIE_SECURE = true;
    private static final String COOKIE_SAMESITE = "Lax";

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request,
                                       String ip,
                                       String userAgent,
                                       HttpServletResponse response) {

        Emp emp = empRepository.findById(request.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getEmpPwd(), emp.getEmpPwd())) {
            throw new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다.");
        }

        // ✅ Access 발급
        String accessToken = jwtTokenProvider.generateToken(emp.getEmpId(), emp.getRole().name());

        // ✅ Refresh 발급 + DB 저장(해시)
        String rawRefresh = RefreshTokenGenerator.generate();
        String refreshHash = RefreshTokenHasher.hash(rawRefresh);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .emp(emp)
                        .tokenHash(refreshHash)
                        .expiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS))
                        .ipAddress(ip)
                        .userAgent(userAgent)
                        .build()
        );

        // ✅ refresh를 HttpOnly 쿠키로 세팅
        CookieUtil.addRefreshCookie(response, rawRefresh, COOKIE_SECURE, COOKIE_SAMESITE, REFRESH_MAX_AGE);

        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(ACCESS_EXPIRES_IN)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthDto.MeResponse me(String empId) {
        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + empId));

        return AuthDto.MeResponse.builder()
                .empId(emp.getEmpId())
                .empName(emp.getEmpName())
                .role(emp.getRole().name())
                .airlineId(emp.getAirlineId() != null ? emp.getAirlineId().getAirlineId() : null)
                .build();
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) return;

        String hash = RefreshTokenHasher.hash(rawRefreshToken);

        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            rt.revoke(LocalDateTime.now()); // 엔티티 도메인 메서드
        });
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        CookieUtil.clearRefreshCookie(response, COOKIE_SECURE, COOKIE_SAMESITE);
    }
}