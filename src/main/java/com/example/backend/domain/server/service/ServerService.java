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
        // 1. Repository에서 한 번의 쿼리로 모든 데이터를 가져옴
        List<ServerEntity> servers = serverRepository.findAll();

        // 2. DTO 변환 (이미 DTO 내부에 대표 이미지 추출 로직이 있으므로 활용)
        return servers.stream()
                .map(ServerResponseDto::from)
                .toList();
    }

    // 3. 특정 서버 상세 조회
    public ServerResponseDto getServer(Long serverId) {
        // 상세 조회 시에도 이미지가 필요하므로 Optional 체크
        ServerEntity serverEntity = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 서버를 찾을 수 없습니다. ID: " + serverId));

        return ServerResponseDto.from(serverEntity);
    }

    // 4. 서버 정보 수정
    @Transactional
    public void updateServer(Long serverId, ServerRequestDto requestDto) {
        ServerEntity serverEntity = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));

        // 1. 기본 정보 부분 수정
        if (requestDto.getName() != null) serverEntity.setName(requestDto.getName());
        if (requestDto.getDescription() != null) serverEntity.setDescription(requestDto.getDescription());
        if (requestDto.getDomain() != null) serverEntity.setDomain(requestDto.getDomain());
        if (requestDto.getVersion() != null) serverEntity.setVersion(requestDto.getVersion());

        // 2. 카테고리 수정 (요청이 있을 때만)
        if (requestDto.getCategoryIds() != null) {
            // 1. 기존 관계를 비움
            serverEntity.clearCategories();

            // 2. 비워진 상태를 DB에 즉시 반영하여 세션 충돌 방지
            serverRepository.saveAndFlush(serverEntity);

            // 3. 새로운 카테고리 추가
            requestDto.getCategoryIds().forEach(categoryId -> {
                CategoryEntity category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
                serverEntity.addCategory(category);
            });
        }
    }

    // 5. 서버 삭제 (S3 이미지 동시 삭제 로직 추가 ⭐)
    @Transactional
    public void deleteServer(Long serverId) {
        ServerEntity serverEntity = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 서버를 찾을 수 없습니다. ID: " + serverId));

        // 1. 서버에 연결된 모든 이미지를 S3 창고에서도 실제 삭제
        serverEntity.getServerImageEntities().forEach(image -> {
            s3Service.deleteImage(image.getImageUrl());
        });

        // 2. 서버 데이터 삭제 (CascadeType.ALL 설정으로 연관된 이미지/카테고리 레코드도 함께 삭제됨)
        serverRepository.delete(serverEntity);
    }
}