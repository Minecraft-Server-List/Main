package com.example.backend.domain.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MinecraftServerStatus {

    private boolean online;
    private Players players;

    @Getter
    @NoArgsConstructor
    public static class Players {
        private int online;
        private int max;
    }
}