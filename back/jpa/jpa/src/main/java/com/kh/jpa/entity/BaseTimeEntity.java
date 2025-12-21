package com.kh.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class) //Auditing(자동으로 값을 매핑)
@MappedSuperclass // 추후에 BaseTimeEntity를 상속한 엔티티의 필드를 컬럼으로 인식.
public abstract class BaseTimeEntity {

    @CreatedDate //여기에 생성시간을 넣겠다.
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate //여기에는 수정시간을 넣겠다.
    private LocalDateTime modifyDate;
}
