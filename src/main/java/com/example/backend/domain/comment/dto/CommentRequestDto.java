package com.example.backend.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private String content; // 1. 댓글 내용
    private Long parentId;  // 2. 부모 댓글 ID (대댓글일 경우 필수)
}