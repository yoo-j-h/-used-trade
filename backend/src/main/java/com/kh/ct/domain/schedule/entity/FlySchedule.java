package com.kh.ct.domain.schedule.entity;

import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "fly_schedule")
public class FlySchedule extends BaseTimeEntity {

    @Id
    @Column(name = "fly_schedule_id")
    private Long flyScheduleId;

    /**
     * ALL_SCHEDULE 과 1:1
     * 같은 PK를 공유하지만 MapsId는 쓰지 않음 (지금 구조에선 불필요 + 위험)
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fly_schedule_id", referencedColumnName = "schedule_id")
    private AllSchedule schedule;

    @Column(name = "airline_id", nullable = false)
    private Long airlineId;

    @Column(name = "flight_number", length = 20)
    private String flightNumber;

    @Column(name = "airplane_type", length = 30)
    private String airplaneType;

    @Column(name = "departure", length = 50)
    private String departure;

    @Column(name = "fly_start_time")
    private LocalDateTime flyStartTime;

    @Column(name = "destination", length = 50)
    private String destination;

    @Column(name = "fly_end_time")
    private LocalDateTime flyEndTime;

    @Column(name = "gate", length = 20)
    private String gate;

    @Column(name = "crew_count")
    private Long crewCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_status", nullable = false)
    private CommonEnums.flightStatus flightStatus;

    @Column(name = "seat_count")
    private Long seatCount;
}
