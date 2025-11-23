package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class ServerDTO {

    private Long serverId;
    private String name;
    private String status;
    private String version;
    private String domain;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    /*
        생성자
     */

    public ServerDTO(String name, String status, String version, String domain) {
        this.name = name;
        this.status = status;
        this.version = version;
        this.domain = domain;
    }

    /*
            getter 및 setter
        */
    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
