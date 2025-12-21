package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(length = 100, nullable = false)
    private String boardTitle;

    @Lob
    @Column(nullable = false)
    private String boardContent;

    @Column(length = 20, nullable = false)
    private String category;

    @Column(length = 100, nullable = false)
    private String region;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 10, nullable = false)
    @Builder.Default
    private String saleStatus = "판매중";

    @Column(length = 500)
    private String imageUrl;

    @Builder.Default
    private Integer count = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_writer", nullable = false)
    private Member member;

    public void changeMember(Member member) {
        if (this.member != null) {
            this.member.getBoards().remove(this);
        }
        this.member = member;
        if (member != null && !member.getBoards().contains(this)) {
            member.getBoards().add(this);
        }
    }

    public void changeImageUrl(String imageUrl) {
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    public void increaseCount() {
        this.count = (this.count == null) ? 1 : this.count + 1;
    }

    public void patchUpdate(String boardTitle, String boardContent, String category,
                            Integer price, String saleStatus) {
        if (boardTitle != null) this.boardTitle = boardTitle;
        if (boardContent != null) this.boardContent = boardContent;
        if (category != null) this.category = category;
        if (price != null) this.price = price;
        if (saleStatus != null) this.saleStatus = saleStatus;
    }
}
