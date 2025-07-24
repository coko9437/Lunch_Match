package com.example.kmj.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String writer;

    @Column(name = "is_blinded", columnDefinition = "boolean default false")
    private boolean isBlinded = false;


    @Column(name = "blind_reason")
    private String blindReason;

    @Column(name = "blind_date")
    private LocalDateTime blindDate;

    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;

    @Column(name = "original_title")
    private String originalTitle;
    // 블라인드 처리 메서드
    public void blindPost(String reason) {
        if (!this.isBlinded) {
            this.originalTitle = this.title;
            this.originalContent = this.content;
            this.title = "경고";
            this.content = "경고";
            this.isBlinded = true;
            this.blindReason = reason;
            this.blindDate = LocalDateTime.now();
        }
    }

    // 블라인드 해제 메서드
    /**
     * 블라인드 처리된 게시글을 해제합니다.
     * 블라인드 처리된 상태이고 원본 제목과 내용이 존재하면,
     * 제목과 내용을 원본으로 되돌리고 블라인드 상태를 false로 설정합니다.
     */
    public void unblindPost() {
        if (this.isBlinded && this.originalTitle != null && this.originalContent != null) {
            this.title = this.originalTitle;
            this.content = this.originalContent;
            this.isBlinded = false;
            this.blindReason = null;
            this.blindDate = null;
            this.originalTitle = null;
            this.originalContent = null;
        }
    }
}
