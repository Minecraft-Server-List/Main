package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class CacheEntry {

    private final ServerStatusDTO status;
    private final LocalDateTime timestamp; // 캐시된 시간

    // 생성자: 상태를 받으면 현재 시간을 타임스탬프로 설정
    public CacheEntry(ServerStatusDTO status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getter
    public ServerStatusDTO getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}