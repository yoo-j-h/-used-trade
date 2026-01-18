package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeDetailId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Code code;

    @Column(nullable = false)
    private String codeDetailName;

    private String codeDesc;
}