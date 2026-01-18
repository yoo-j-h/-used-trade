package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmpHealth extends BaseTimeEntity {

    @Id
    private String empId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Emp emp;

    private Integer healthPoint;
    private Integer stressPoint;
    private Integer fatiguePoint;
    private Integer physicalPoint;
}
