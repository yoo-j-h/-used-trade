package com.kh.ct.domain.schedule.entity;

import com.kh.ct.domain.emp.entity.AirlineAirport;
import com.kh.ct.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Airport extends BaseTimeEntity{
    @Id
    private String airportCode;

    @Column(length = 100)
    private String airportName;

    @Column(length = 100)
    private String countryName;

    @Column(length = 100)
    private String timezone;

    @Column(length = 100)
    private String cityName;

    @OneToMany(mappedBy = "airportId", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AirlineAirport> airlineAirports = new ArrayList<>();
}
