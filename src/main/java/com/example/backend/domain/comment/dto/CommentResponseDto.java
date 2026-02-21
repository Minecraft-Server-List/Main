package com.example.backend.domain.comment.dto;

import com.example.backend.domain.comment.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> replies; // 3. 대댓글 목록 (재귀 구조)

    public static CommentResponseDto from(CommentEntity comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .authorName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .replies(comment.getChildren().stream()
                        .map(CommentResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}