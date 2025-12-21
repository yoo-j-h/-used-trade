package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "PROFILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(length = 100)
    private String profileImage;

    @Column(length = 300)
    private String intro;

    //==== 연관관계 맵핑 ====

    //프로필 : 회원 (1 : 1) - 연관관계 주인으로 Profile을 사용, 반대로 해도 됨
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Member member;
}
