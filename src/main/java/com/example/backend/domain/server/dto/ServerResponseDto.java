package com.example.backend.domain.server.dto;

import com.example.backend.domain.server.entity.ServerEntity;
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
    private String fileName;
    private LocalDateTime createdAt;

    public static ServerResponseDto from(ServerEntity server) {
        List<String> categoryNames = server.getServerCategories().stream()
                .map(sc -> sc.getCategory().getName())
                .collect(Collectors.toList());

        String firstFileName = server.getServerImages().stream()
                .findFirst()
                .map(img -> img.getFileName())
                .orElse(null);

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
                firstFileName,
                server.getCreatedAt()
        );
    }
}