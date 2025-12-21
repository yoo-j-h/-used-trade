package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(length = 500)
    private String profileImage;

    @Column(length = 300)
    private String intro;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Member member;

    public void changeMember(Member member) {
        this.member = member;
    }

    public void patchUpdate(String profileImage, String intro) {
        if (profileImage != null) this.profileImage = profileImage;
        if (intro != null) this.intro = intro;
    }
}
