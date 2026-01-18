package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmpSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empScheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AllSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp emp;
}