package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmpSurvey extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empSurveyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp emp;

    private Integer flyStressPoint;
    private Integer timeDifferencePoint;
    private Integer workPatternPoint;
}