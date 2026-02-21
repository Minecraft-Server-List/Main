package com.example.backend.domain.comment.service;

import com.example.backend.domain.comment.dto.CommentRequestDto;
import com.example.backend.domain.comment.dto.CommentResponseDto;
import com.example.backend.domain.comment.entity.CommentEntity;
import com.example.backend.domain.comment.repository.CommentRepository;
import com.example.backend.domain.post.entity.PostEntity;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 1. 댓글/대댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long postId, Long userId, CommentRequestDto requestDto) {
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        CommentEntity parent = null;
        if (requestDto.getParentId() != null) {
            parent = commentRepository.findById(requestDto.getParentId()).orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
        }

        CommentEntity comment = CommentEntity.builder()
                .content(requestDto.getContent()).post(post).user(user).parent(parent)
                .build();

        return CommentResponseDto.from(commentRepository.save(comment));
    }

    // 2. 게시글별 댓글 전체 조회
    public List<CommentResponseDto> getCommentsByPost(Long postId) {
        return commentRepository.findByPost_PostIdAndParentIsNull(postId).stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }

    // 3. 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        comment.update(requestDto.getContent());
        return CommentResponseDto.from(comment);
    }

    // 4. 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
