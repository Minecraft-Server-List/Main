package com.example.backend.domain.post.repository;

import com.example.backend.domain.post.entity.PostClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostClassificationRepository extends JpaRepository<PostClassificationEntity, Long> {

    // 1. 게시판 및 카테고리 매핑

}