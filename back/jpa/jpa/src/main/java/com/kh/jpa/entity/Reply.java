// src/main/java/com/kh/jpa/entity/Reply.java
package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyNo;

    @Column(length = 400, nullable = false)
    private String replyContent;

    @Enumerated(EnumType.STRING)
    @Column(length = 1, nullable = false)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.Y;

    // 게시글
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ref_bno", nullable = false)
    private Board board;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reply_writer", nullable = false)
    private Member member;

    // ===== 비즈니스 메서드 =====
    public void changeBoard(Board board) {
        this.board = board;
    }

    public void changeMember(Member member) {
        this.member = member;
    }

    public void patchUpdate(String replyContent) {
        if (replyContent != null) {
            this.replyContent = replyContent;
        }
    }

    public void deactivate() {
        this.status = CommonEnums.Status.N;
    }
}
