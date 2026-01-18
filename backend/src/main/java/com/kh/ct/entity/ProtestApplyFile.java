package com.kh.ct.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProtestApplyFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long protestApplyFileId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProtestApply protestApply;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private File file;
}