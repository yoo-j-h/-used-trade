package com.kh.jpa.repository;

import com.kh.jpa.entity.Member;
import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Member member) {
        em.persist(member);
    }

    @Override
    public List<Member> findAll() {
        String jpql = "select m from Member m where m.status = :status";
        return em.createQuery(jpql, Member.class)
                .setParameter("status", CommonEnums.Status.Y)
                .getResultList();
    }

    @Override
    public Optional<Member> findById(String userId) {
        String jpql = "select m from Member m where m.userId = :userId and m.status = :status";
        List<Member> result = em.createQuery(jpql, Member.class)
                .setParameter("userId", userId)
                .setParameter("status", CommonEnums.Status.Y)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public void delete(Member member) {
        // ✅ 소프트 삭제: status -> N (dirty checking)
        Member managed = em.contains(member) ? member : em.find(Member.class, member.getUserId());
        if (managed != null) {
            managed.deactivate();
        }
    }

    @Override
    public List<Member> findByUserNameContaining(String keyword) {
        String jpql = "select m from Member m where m.userName like :keyword and m.status = :status";
        return em.createQuery(jpql, Member.class)
                .setParameter("keyword", "%" + keyword + "%")
                .setParameter("status", CommonEnums.Status.Y)
                .getResultList();
    }
    @Override
    public Optional<Member> findActiveByUserIdAndUserPwd(String userId, String userPwd) {
        String jpql = """
            select m
            from Member m
            where m.userId = :userId
              and m.userPwd = :userPwd
              and m.status = :status
            """;

        return em.createQuery(jpql, Member.class)
                .setParameter("userId", userId)
                .setParameter("userPwd", userPwd)
                .setParameter("status", CommonEnums.Status.Y)
                .getResultStream()
                .findFirst();
    }

}
