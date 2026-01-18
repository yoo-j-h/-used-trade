package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProgramApply extends BaseTimeEntity {

    @Id
    @Column(length = 255)
    private String programApplyId;

    @Column(length = 50)
    private String programCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp manager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp approver;

    @Lob
    private String programApplyReason;

    private LocalDateTime programApplyDate;

    @Column(length = 30)
    private String programApplyStatus;

    @Lob
    private String programApplyCancelReason;

    @OneToOne(mappedBy = "programApply",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Program program;
}
