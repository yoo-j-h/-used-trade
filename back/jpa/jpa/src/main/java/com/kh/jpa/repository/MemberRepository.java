package com.kh.jpa.repository;

import com.kh.jpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    void save(Member member);
    List<Member> findAll();                    // status=Y만
    Optional<Member> findById(String userId);  // status=Y만
    void delete(Member member);                // 소프트 삭제
    List<Member> findByUserNameContaining(String keyword); // status=Y만
}
