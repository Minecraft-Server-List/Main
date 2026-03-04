package com.example.backend.domain.server.service;

import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.domain.category.repository.CategoryRepository;
import com.example.backend.domain.server.dto.ServerRequestDto;
import com.example.backend.domain.server.dto.ServerResponseDto;
import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.repository.ServerRepository;
import com.example.backend.global.config.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;

    // 1. 서버 등록
    @Transactional
    public ServerResponseDto createServer(ServerRequestDto requestDto) {
        // 기본 상태는 OFFLINE으로 시작 (스케줄러가 곧 ONLINE으로 바꿔줄 거예요)
        ServerEntity serverEntity = serverRepository.save(requestDto.toEntity());

        if (requestDto.getCategoryIds() != null && !requestDto.getCategoryIds().isEmpty()) {
            requestDto.getCategoryIds().stream().distinct().forEach(categoryId -> {
                CategoryEntity category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + categoryId));
                serverEntity.addCategory(category);
            });
        }

        return ServerResponseDto.from(serverEntity);
    }

    // 2. 서버 전체 목록 조회
    public List<ServerResponseDto> getAllServers() {
        // Repository의 @EntityGraph 덕분에 N+1 문제 없이 한 번에 가져옴
        List<ServerEntity> servers = serverRepository.findAll();

        return servers.stream()
                .map(ServerResponseDto::from)
                .toList();
    }

    // 3. 특정 서버 상세 조회
    public ServerResponseDto getServer(Long serverId) {
        // findById 대신 성능 최적화된 findByServerId 사용
        ServerEntity serverEntity = serverRepository.findByServerId(serverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 서버를 찾을 수 없습니다. ID: " + serverId));

        return ServerResponseDto.from(serverEntity);
    }

    // 4. 서버 정보 수정
    @Transactional
    public void updateServer(Long serverId, ServerRequestDto requestDto) {
        ServerEntity serverEntity = serverRepository.findByServerId(serverId)
                .orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));

        // 1. 기본 정보 및 ENUM 상태 수정
        if (requestDto.getName() != null) serverEntity.setName(requestDto.getName());
        if (requestDto.getDescription() != null) serverEntity.setDescription(requestDto.getDescription());
        if (requestDto.getDomain() != null) serverEntity.setDomain(requestDto.getDomain());
        if (requestDto.getVersion() != null) serverEntity.setVersion(requestDto.getVersion());

        // 추가된 필드들 반영
        if (requestDto.getStatus() != null) serverEntity.setStatus(requestDto.getStatus());
        if (requestDto.getCurrentPlayers() != null) serverEntity.setCurrentPlayers(requestDto.getCurrentPlayers());
        if (requestDto.getMaxPlayers() != null) serverEntity.setMaxPlayers(requestDto.getMaxPlayers());
        serverEntity.setLastCheckedAt(LocalDateTime.now());

        // 2. 카테고리 수정
        if (requestDto.getCategoryIds() != null) {
            serverEntity.clearCategories();

            // 기존 데이터 즉시 삭제 반영 (영속성 충돌 방지)
            serverRepository.saveAndFlush(serverEntity);

            requestDto.getCategoryIds().forEach(categoryId -> {
                CategoryEntity category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + categoryId));
                serverEntity.addCategory(category);
            });
        }
    }

    // 5. 서버 삭제 (S3 동시 삭제 유지 ⭐)
    @Transactional
    public void deleteServer(Long serverId) {
        ServerEntity serverEntity = serverRepository.findByServerId(serverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 서버를 찾을 수 없습니다. ID: " + serverId));

        // S3 실물 파일 삭제 로직
        serverEntity.getServerImageEntities().forEach(image -> {
            s3Service.deleteImage(image.getImageUrl());
        });

        // 레코드 삭제 (ON DELETE CASCADE로 DB 자식들도 싹 지워짐)
        serverRepository.delete(serverEntity);
    }
}