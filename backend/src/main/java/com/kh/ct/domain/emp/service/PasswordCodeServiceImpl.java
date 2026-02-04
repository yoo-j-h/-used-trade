package com.kh.ct.domain.emp.service;

import com.kh.ct.domain.emp.entity.PasswordCode;
import com.kh.ct.domain.emp.repository.PasswordCodeRepository;
import com.kh.ct.domain.emp.service.PasswordCodeService;
import com.kh.ct.global.common.CommonEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordCodeServiceImpl implements PasswordCodeService {

    private final PasswordCodeRepository passwordCodeRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    private String generate6DigitCode() {
        int n = RANDOM.nextInt(900000) + 100000; // 100000~999999
        return String.valueOf(n);
    }

    @Override
    @Transactional
    public void sendCode(String email) {
        String code = generate6DigitCode();

        // ✅ 테스트용 로그 출력 (운영에서는 절대 금지)
        log.info("[TEST] PasswordCode email={} code={}", email, code);

        // ✅ 엔티티 수정 금지 조건이므로 그대로 저장
        // codeExpiresDate는 제한시간 로직을 쓰지 않으므로 의미 없는 값으로 넣어둠
        PasswordCode entity = PasswordCode.builder()
                .email(email)
                .passwordCode(code)
                .codeExpiresDate(LocalDateTime.now())
                .passwordCodeStatus(CommonEnums.CommonStatus.Y) // ✅ 실제 enum 값으로 조정 필요
                .build();

        passwordCodeRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyCode(String email, String code) {
        PasswordCode latest = passwordCodeRepository
                .findTopByEmailOrderByCreateDateDesc(email)
                .orElse(null);

        if (latest == null) {
            log.info("[TEST] verify failed: no code found for email={}", email);
            return false;
        }
        if (code == null) {
            log.info("[TEST] verify failed: code is null email={}", email);
            return false;
        }

        boolean ok = code.equals(latest.getPasswordCode());
        log.info("[TEST] verify result email={} ok={}", email, ok);
        return ok;
    }
}