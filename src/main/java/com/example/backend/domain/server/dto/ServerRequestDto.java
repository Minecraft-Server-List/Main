package com.example.backend.domain.server.dto;

import com.example.backend.domain.server.entity.ServerEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ServerRequestDto {
    private String name;
    private String description;
    private String domain;
    private String version;
    private List<Long> categoryIds;

    public ServerEntity toEntity() {
        return ServerEntity.builder()
                .name(this.name)
                .description(this.description)
                .domain(this.domain)
                .status("OFFLINE") // 초기 상태는 무조건 OFFLINE
                .currentPlayers(0)
                .maxPlayers(0)
                .build();
    }
}
