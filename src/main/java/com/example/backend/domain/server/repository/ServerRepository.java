package com.example.backend.domain.server.repository;

import com.example.backend.domain.server.entity.ServerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<ServerEntity, Long> {

    // 1. 목록 조회 시 카테고리와 이미지를 Join해서 한 번에 가져오기
    @Override
    @EntityGraph(attributePaths = {"serverCategories", "serverImageEntities"})
    List<ServerEntity> findAll();

    // 2. 단건 상세 조회 시에도 N+1 방지를 위해 Fetch Join 적용
    @EntityGraph(attributePaths = {"serverCategories", "serverImageEntities"})
    Optional<ServerEntity> findByServerId(Long serverId);

    // 3. 도메인 중복 체크용
    Optional<ServerEntity> findByDomain(String domain);
}