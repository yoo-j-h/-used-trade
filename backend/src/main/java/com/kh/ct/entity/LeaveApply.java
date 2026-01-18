package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveApply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveApplyId;

    @Column(length = 50)
    private String leaveApplyCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp applicant;

    private LocalDateTime leaveStartDate;

    @Lob
    private String leaveApplyReason;

    private LocalDateTime leaveEndDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp approver;

    @Column(length = 30)
    private String leaveApplyStatus;

    @Lob
    private String leaveApplyCancelReason;
}