package com.example.backend.domain.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinecraftServerStatus {

    @JsonProperty("online")
    private boolean isOnline;

    @JsonProperty("players")
    private PlayerInfo playerInfo;

    @JsonProperty("version")
    private VersionInfo versionInfo;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerInfo {
        @JsonProperty("online")
        private int currentCount;

        @JsonProperty("max")
        private int maxCount;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VersionInfo {
        @JsonProperty("name_clean") // mcstatus.io에 맞춘 매핑이지만, 필드명은 범용적임
        private String versionName;
    }
}
