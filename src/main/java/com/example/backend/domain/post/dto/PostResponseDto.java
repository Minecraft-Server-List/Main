package com.example.backend.domain.post.dto;

import com.example.backend.domain.post.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String authorName;
    private Integer viewCount;
    private Integer likeCount;
    private List<String> categories;
    private LocalDateTime createdAt;

    public static PostResponseDto from(PostEntity post) {
        return new PostResponseDto(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getName(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getPostClassifications().stream()
                        .map(pc -> pc.getCategory().getName())
                        .collect(Collectors.toList()),
                post.getCreatedAt()
        );
    }
}