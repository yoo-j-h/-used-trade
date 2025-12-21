package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @Column(length = 30)
    private String userId;

    @Column(length = 100, nullable = false)
    private String userPwd;

    @Column(length = 15, nullable = false)
    private String userName;

    @Column(length = 255)
    private String email;

    @Column(length = 13)
    private String phone;

    @Column(length = 100)
    private String address;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.Y;

    // ==== 연관관계 맵핑 ====
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<Board> boards = new ArrayList<>();

    //회원정보 전체수정 메서드
    public void putUpdate(String userName, String email, String phone, String address) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    //회원정보 부분수정 메서드
    public void patchUpdate(String userName, String email, String phone, String address) {
        if (userName != null) this.userName = userName;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
        if (address != null) this.address = address;
    }

    public void deactivate() {
        this.status = CommonEnums.Status.N;
    }
}
