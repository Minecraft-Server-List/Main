package com.example.backend.domain.server.repository;

import com.example.backend.domain.server.entity.ServerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<ServerEntity, Long> {

    // 1. findAll 호출 시 카테고리와 이미지를 한 번에 Join해서 가져오기 (N+1 방지)
    @Override
    @EntityGraph(attributePaths = {"serverCategories", "serverImageEntities"})
    List<ServerEntity> findAll();

}