package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlySchedule extends BaseTimeEntity {

    @Id
    private Long scheduleId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private AllSchedule allSchedule;

    @Column(length = 20)
    private String flightNumber;

    @Column(length = 30)
    private String airplaneType;

    @Column(length = 50)
    private String departure;

    private LocalDateTime flyStartTime;

    @Column(length = 50)
    private String destination;

    private LocalDateTime flyEndTime;

    @Column(length = 20)
    private String gate;

    private Integer crewCount;
}