package com.example.backend.domain.server.entity;

import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ServerEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serverId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String status;

    @Column(length = 50)
    private String version;

    @Column(length = 30)
    private String domain;

    private Integer currentPlayers;
    private Integer maxPlayers;
    private LocalDateTime lastCheckedAt;

    @Builder.Default
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerCategoryEntity> serverCategories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerImageEntity> serverImages = new ArrayList<>();

    // 서버 정보 수정
    public void update(String name, String description, String domain) {
        this.name = name;
        this.description = description;
        this.domain = domain;
    }

    // 스케줄러 서버 정보 업데이트
    public void updateStatus(String status, int currentPlayers, int maxPlayers) {
        this.status = status;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
    }

    public void addCategory(CategoryEntity category) {
        ServerCategoryEntity serverCategory = ServerCategoryEntity.builder()
                .server(this)
                .category(category)
                .build();
        this.serverCategories.add(serverCategory);
    }
}