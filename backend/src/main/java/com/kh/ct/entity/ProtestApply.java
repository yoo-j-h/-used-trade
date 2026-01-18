package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProtestApply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long protestApplyId;

    private LocalDateTime protestApplyDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp approver;

    @Column(length = 30)
    private String protestApplyStatus;

    @Lob
    private String protestApplyCancelReason;

    @Column(length = 30)
    private String protestAttendanceStatus;

    @OneToMany(mappedBy = "protestApply", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProtestApplyFile> files = new ArrayList<>();
}
