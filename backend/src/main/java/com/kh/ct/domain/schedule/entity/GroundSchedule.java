package com.kh.ct.domain.schedule.entity;


import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroundSchedule extends BaseTimeEntity {
    @Id
    @Column(name = "ground_schedule_id")
    private Long groundScheduleId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ground_schedule_id")
    private AllSchedule scheduleId;

    @JoinColumn(name = "emp_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp empId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.CommonStatus scheduleStatus;

    @Column(length = 40)
    private String workCode;

    private LocalDate scheduleStartDate;

    private LocalDate scheduleEndDate;

    private LocalTime scheduleStartTime;

    private LocalTime scheduleEndTime;
}
