package com.example.backend.domain.server.dto;

import com.example.backend.domain.server.entity.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ServerResponseDto {
    private Long serverId;
    private String name;
    private String description;
    private String domain;
    private String status;
    private String version;
    private Integer currentPlayers;
    private Integer maxPlayers;
    private List<String> categories;

    // 1. imageUrl 리스트 또는 대표 이미지 URL 사용
    private List<String> imageUrls;
    private String representativeImageUrl; // 목록 화면용 대표 이미지

    private LocalDateTime createdAt;

    public static ServerResponseDto from(Server server) {
        // 2. 카테고리 이름 목록 추출
        List<String> categoryNames = server.getServerCategories().stream()
                .map(sc -> sc.getCategory().getName())
                .collect(Collectors.toList());

        // 3. 이미지 엔티티에서 CloudFront URL 목록만 추출
        List<String> urls = server.getServerImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());

        // 4. 대표 이미지 URL 추출 (첫 번째 이미지)
        String firstUrl = urls.isEmpty() ? null : urls.get(0);

        return new ServerResponseDto(
                server.getServerId(),
                server.getName(),
                server.getDescription(),
                server.getDomain(),
                server.getStatus(),
                server.getVersion(),
                server.getCurrentPlayers(),
                server.getMaxPlayers(),
                categoryNames,
                urls,        // 이미지 전체 리스트
                firstUrl,    // 대표 이미지
                server.getCreatedAt()
        );
    }
}