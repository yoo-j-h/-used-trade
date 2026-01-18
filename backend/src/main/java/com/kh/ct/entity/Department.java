package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(length = 100)
    private String departmentName;

    @Column(length = 50) // 숫자를 문자열로 들고 있길래 일단 짧게
    private String empCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department parentDepartment;

    @OneToMany(mappedBy = "parentDepartment", fetch = FetchType.LAZY)
    private List<Department> children = new ArrayList<>();
}