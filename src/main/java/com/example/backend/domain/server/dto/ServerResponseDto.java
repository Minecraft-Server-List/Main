package com.example.backend.domain.server.dto;

import com.example.backend.domain.server.entity.ServerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class ServerResponseDto {
    private Long serverId;
    private String name;
    private String description;
    private String domain;

    private String status;            // "ONLINE", "OFFLINE" 등 (영문 코드)
    private String statusDescription; // "온라인", "오프라인" 등 (한글 설명)

    private String version;
    private Integer currentPlayers;
    private Integer maxPlayers;
    private List<String> categories;

    private List<String> imageUrls;
    private String representativeImageUrl;

    private LocalDateTime createdAt;

    public static ServerResponseDto from(ServerEntity serverEntity) {
        // 1. 카테고리 이름 목록 추출
        List<String> categoryNames = serverEntity.getServerCategories().stream()
                .map(sc -> sc.getCategory().getName())
                .collect(Collectors.toList());

        // 2. 이미지 URL 목록 추출 (이미지 엔티티 리스트 이름 확인 필요: serverImageEntities)
        List<String> urls = serverEntity.getServerImageEntities().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());

        String firstUrl = urls.isEmpty() ? null : urls.get(0);

        // 3. 빌더를 사용해 객체 생성 (필드가 많을 때 실수 방지)
        return ServerResponseDto.builder()
                .serverId(serverEntity.getServerId())
                .name(serverEntity.getName())
                .description(serverEntity.getDescription())
                .domain(serverEntity.getDomain())
                .status(serverEntity.getStatus().name()) // // ENUM의 이름 (예: "OFFLINE")
                .statusDescription(serverEntity.getStatus().getDescription()) // // ENUM의 한글 설명 (예: "오프라인")
                .version(serverEntity.getVersion())
                .currentPlayers(serverEntity.getCurrentPlayers())
                .maxPlayers(serverEntity.getMaxPlayers())
                .categories(categoryNames)
                .imageUrls(urls)
                .representativeImageUrl(firstUrl)
                .createdAt(serverEntity.getCreatedAt())
                .build();
    }
}