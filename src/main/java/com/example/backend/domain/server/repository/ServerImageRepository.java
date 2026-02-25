package com.example.backend.domain.server.repository;

import com.example.backend.domain.server.entity.ServerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerImageRepository extends JpaRepository<ServerImage, Long> {

    // 특정 서버에 등록된 모든 이미지 리스트를 가져올 때 사용합니다.
    List<ServerImage> findByServer_ServerId(Long serverId);
}