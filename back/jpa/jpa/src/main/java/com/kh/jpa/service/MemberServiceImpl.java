package com.kh.jpa.service;

import com.kh.jpa.dto.MemberDto;
import com.kh.jpa.entity.Member;
import com.kh.jpa.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public String createMember(MemberDto.Create createMemberDto) {
        Member member = createMemberDto.toEntity();
        memberRepository.save(member);
        return member.getUserId();
    }

    @Override
    public List<MemberDto.Response> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(member -> MemberDto.Response.of(
                        member.getUserId(),
                        member.getUserName(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getAddress(),
                        member.getCreateDate(),
                        member.getModifyDate()
                ))
                .toList();
    }

    @Override
    public MemberDto.Response getMemberByUserId(String userId) {
        return memberRepository.findById(userId)
                .map(member -> MemberDto.Response.of(
                        member.getUserId(),
                        member.getUserName(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getAddress(),
                        member.getCreateDate(),
                        member.getModifyDate()
                ))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Override
    public MemberDto.Response updateMember(String userId, MemberDto.Update updateMemberDto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.putUpdate(
                updateMemberDto.getUser_name(),
                updateMemberDto.getEmail(),
                updateMemberDto.getPhone(),
                updateMemberDto.getAddress()
        );

        return MemberDto.Response.of(
                member.getUserId(),
                member.getUserName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getCreateDate(),
                member.getModifyDate()
        );
    }

    @Override
    public void deleteMember(String userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));


        memberRepository.delete(member);
    }

    @Override
    public List<MemberDto.Response> getMembersByName(String keyword) {
        return memberRepository.findByUserNameContaining(keyword)
                .stream()
                .map(member -> MemberDto.Response.of(
                        member.getUserId(),
                        member.getUserName(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getAddress(),
                        member.getCreateDate(),
                        member.getModifyDate()
                ))
                .toList();
    }
    // src/main/java/com/kh/jpa/service/MemberServiceImpl.java
    @Override
    public MemberDto.Response login(MemberDto.Login loginDto) {
        Member member = memberRepository.findActiveByUserIdAndUserPwd(
                        loginDto.getUser_id(),
                        loginDto.getUser_pwd()
                )
                .orElseThrow(() -> new EntityNotFoundException("아이디 또는 비밀번호가 올바르지 않습니다."));

        return MemberDto.Response.of(
                member.getUserId(),
                member.getUserName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getCreateDate(),
                member.getModifyDate()
        );
    }

}
