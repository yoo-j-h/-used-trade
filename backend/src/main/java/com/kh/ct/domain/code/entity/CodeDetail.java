package com.kh.ct.domain.code.entity;

import com.kh.ct.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "CODE_DETAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CodeDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeDetailId;

    @JoinColumn(name = "CODE_ID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Code codeId;

    @Column(nullable = false)
    private String codeDetailName;

    @Lob
    private String codeDesc;
}