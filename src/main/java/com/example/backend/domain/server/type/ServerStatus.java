package com.example.backend.domain.server.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerStatus {

    ONLINE("온라인"),
    OFFLINE("오프라인"),
    MAINTENANCE("점검 중");

    private final String description;
}