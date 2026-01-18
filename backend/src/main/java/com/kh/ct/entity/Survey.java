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
public class Survey extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyId;

    private LocalDateTime surveyFrom;

    @Column(nullable = false)
    private LocalDateTime surveyTo;

    @Column(nullable = false, length = 20) // PROGRESS, COMPLETE
    private String surveyState;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp writer;

    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpSurvey> empSurveys = new ArrayList<>();
}