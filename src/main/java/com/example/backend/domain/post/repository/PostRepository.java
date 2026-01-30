package com.example.backend.domain.post.repository;

import com.example.backend.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // 1. 기본 CRUD 제공

}