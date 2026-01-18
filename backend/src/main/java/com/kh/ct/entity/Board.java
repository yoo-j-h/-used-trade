package com.kh.ct.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String boardType;

    @Column(nullable = false, length = 100)
    private String boardTitle;

    @Lob
    @Column(nullable = false)
    private String boardContent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Emp writer;
}