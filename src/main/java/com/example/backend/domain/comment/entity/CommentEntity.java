package com.example.backend.domain.comment.entity;

import com.example.backend.domain.post.entity.PostEntity;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId; // 1. 댓글 고유 번호

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 2. 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post; // 3. 게시글 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 4. 작성자 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent; // 5. 부모 댓글 (대댓글용 셀프 참조)

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> children = new ArrayList<>(); // 6. 자식 댓글 리스트

    // 7. 댓글 수정 로직
    public void update(String content) {
        this.content = content;
    }
}