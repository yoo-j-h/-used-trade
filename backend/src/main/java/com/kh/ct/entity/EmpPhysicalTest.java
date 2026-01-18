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
public class EmpPhysicalTest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long physicalTestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp emp;

    @Column(nullable = false)
    private LocalDateTime testDate;

    private Integer length;
    private Integer height;

    @Column(nullable = false)
    private Integer bloodSugar;

    private Integer systolicBloodPressure;
    private Integer cholesterol;
    private Integer diastolicBloodPressure;
    private Integer heartRate;
    private Integer bmi;
    private Integer bodyFat;

    @OneToMany(mappedBy = "physicalTest", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpPhysicalTestFile> files = new ArrayList<>();
}
