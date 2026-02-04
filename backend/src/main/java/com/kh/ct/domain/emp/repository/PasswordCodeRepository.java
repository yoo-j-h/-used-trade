package com.kh.ct.domain.emp.repository;

import com.kh.ct.domain.emp.entity.PasswordCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordCodeRepository extends JpaRepository<PasswordCode, Long> {

    // ✅ 이메일별 최신 코드 1건 조회 (BaseTimeEntity에 createDate가 있다고 가정)
    Optional<PasswordCode> findTopByEmailOrderByCreateDateDesc(String email);
}
