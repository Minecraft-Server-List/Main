package com.example.backend.domain.comment.repository;

import com.example.backend.domain.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // 1. 특정 게시글의 최상위 댓글만 조회 ( 대댓글 child 배열로 포함하여 조회 )
    List<CommentEntity> findByPost_PostIdAndParentIsNull(Long postId);
}