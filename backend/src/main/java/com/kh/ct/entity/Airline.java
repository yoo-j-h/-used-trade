package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Airline extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long airlineId;

    @Column(length = 100)
    private String field;

    @Column(length = 100)
    private String theme;

    @Column(length = 50)
    private String mainNumber;

    @Column(length = 255)
    private String airlineAddress;

    @Column(length = 500)
    private String desc;
}