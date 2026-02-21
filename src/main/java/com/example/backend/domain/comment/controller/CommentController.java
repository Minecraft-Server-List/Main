package com.example.backend.domain.comment.controller;

import com.example.backend.domain.comment.dto.CommentRequestDto;
import com.example.backend.domain.comment.dto.CommentResponseDto;
import com.example.backend.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.global.common.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 작성
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long postId, @RequestParam Long userId, @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(200, commentService.createComment(postId, userId, dto)));
    }

    // 2. 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success(200, commentService.getCommentsByPost(postId)));
    }

    // 3. 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId, @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(200, commentService.updateComment(commentId, dto)));
    }

    // 4. 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }
}
