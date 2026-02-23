package com.kh.ct.domain.auth.controller;

import com.kh.ct.domain.auth.dto.AuthDto;
import com.kh.ct.domain.auth.service.AuthRefreshService;
import com.kh.ct.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthRefreshService authRefreshService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        // ✅ 여기서 refresh 쿠키 세팅까지 authService.login이 처리
        return ResponseEntity.ok(authService.login(request, ip, userAgent, httpResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.RefreshResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        return ResponseEntity.ok(authRefreshService.refresh(refreshToken, ip, userAgent, httpResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        authService.logout(refreshToken);
        authService.clearRefreshCookie(httpResponse);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDto.MeResponse> me(Authentication authentication) {
        String empId = authentication.getName();
        return ResponseEntity.ok(authService.me(empId));
    }
}