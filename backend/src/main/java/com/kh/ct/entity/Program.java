package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Program extends BaseTimeEntity {

    @Id
    @Column(length = 255)
    private String programApplyId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private ProgramApply programApply;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AllSchedule schedule;

    @Lob
    private String programContent;

    @Column(length = 30)
    private String programStatus;

    @Lob
    private String programCancelReason;
}