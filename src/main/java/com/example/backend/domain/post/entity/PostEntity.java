package com.example.backend.domain.post.entity;

import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostClassificationEntity> postClassifications = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}