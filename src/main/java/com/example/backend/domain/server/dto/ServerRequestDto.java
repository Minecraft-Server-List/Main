package com.example.backend.domain.server.dto;

import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.type.ServerStatus; // // ENUM 임포트
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

    // 수동 상태 변경이 필요할 경우를 대비해 필드 추가 (선택사항)
    private ServerStatus status;
    private Integer currentPlayers;
    private Integer maxPlayers;

    public ServerEntity toEntity() {
        return ServerEntity.builder()
                .name(this.name)
                .description(this.description)
                .domain(this.domain)
                .version(this.version)
                .status(ServerStatus.OFFLINE) // // 문자열 대신 ENUM 상수 사용
                .currentPlayers(0)
                .maxPlayers(0)
                .build();
    }
}