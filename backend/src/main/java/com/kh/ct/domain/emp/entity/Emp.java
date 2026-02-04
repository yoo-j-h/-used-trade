package com.kh.ct.domain.emp.entity;

import com.kh.ct.domain.health.entity.EmpHealth;
import com.kh.ct.global.entity.BaseTimeEntity;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.entity.File;
import com.kh.ct.domain.health.entity.EmpPhysicalTest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Emp extends BaseTimeEntity {

    @Id
    @Column(name = "emp_id", length = 50)
    private String empId;

    @JoinColumn(name = "airline_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Airline airlineId;

    @JoinColumn(name = "department_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Department departmentId;

    @Column(nullable = false, length = 100)
    private String empName;

    @Column(nullable = false, length = 255)
    private String empPwd;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)// 승무원/조종사/정비사 등
    private CommonEnums.Role role;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, length = 50)
    private String job;

    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private CommonEnums.EmpStatus empStatus;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Float leaveCount;

    @Column(nullable = false, length = 50, unique = true)
    private String empNo;

    @JoinColumn(name = "profile_Image")
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private File profileImage;

    @OneToMany(mappedBy = "empId", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpHealth> empHealthList = new ArrayList<>();

    @OneToMany(
            mappedBy = "empId", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpPhysicalTest> physicalTests = new ArrayList<>();

    // 비즈니스 로직 메서드
    public void updatePassword(String encodedPassword) {
        this.empPwd = encodedPassword;
    }

    public void updateEmpStatus(CommonEnums.EmpStatus status) {
        this.empStatus = status;
    }

    public void updateAirlineId(Airline airline) {
        this.airlineId = airline;
    }
   //setting 업테이드 로직
   public void updateProfile(String empName, Integer age, String email, String phone, String address) {
       if (empName != null) this.empName = empName;
       if (age != null) this.age = age;
       if (email != null) this.email = email;
       if (phone != null) this.phone = phone;
       if (address != null) this.address = address;
   }


}
