package com.example.backend.domain.server.service;

import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.domain.category.repository.CategoryRepository;
import com.example.backend.domain.server.dto.ServerRequestDto;
import com.example.backend.domain.server.dto.ServerResponseDto;
import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.repository.ServerRepository;
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

    // 1. 서버 등록
    @Transactional
    public ServerResponseDto createServer(ServerRequestDto requestDto) {
        ServerEntity serverEntity = serverRepository.save(requestDto.toEntity());

        if (requestDto.getCategoryIds() != null && !requestDto.getCategoryIds().isEmpty()) {
            requestDto.getCategoryIds().forEach(categoryId -> {
                CategoryEntity category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + categoryId));
                serverEntity.addCategory(category);
            });
        }

        return ServerResponseDto.from(serverEntity);
    }

    // 2-1. 서버 전체 목록 조회
    public List<ServerResponseDto> getAllServers() {
        return serverRepository.findAll().stream()
                .map(ServerResponseDto::from)
                .toList();
    }

    // 2-2. 특정 서버 상세 조회
    public ServerResponseDto getServer(Long serverId) {
        ServerEntity serverEntity = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 서버를 찾을 수 없습니다. ID: " + serverId));
        return ServerResponseDto.from(serverEntity);
    }

    // 3. 서버 정보 수정
    @Transactional
    public ServerResponseDto updateServer(Long serverId, ServerRequestDto requestDto) {
        ServerEntity serverEntity = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("해당 서버를 찾을 수 없습니다. ID: " + serverId));

        // Entity 내부의 update 메서드 활용 (아래 Entity 코드 참고)
        serverEntity.update(requestDto.getName(), requestDto.getDescription(), requestDto.getDomain());

        return ServerResponseDto.from(serverEntity);
    }

    // 4. 서버 삭제
    @Transactional
    public void deleteServer(Long serverId) {
        serverRepository.deleteById(serverId);
    }
}
