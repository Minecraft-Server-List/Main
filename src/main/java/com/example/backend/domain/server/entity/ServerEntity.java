package com.example.backend.domain.server.entity;

import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.domain.server.type.ServerStatus;
import com.example.backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "servers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServerEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serverId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default // // 빌더 사용 시에도 기본값(OFFLINE)이 적용되도록 설정
    private ServerStatus status = ServerStatus.OFFLINE;

    @Column(length = 20)
    private String version;

    @Column(nullable = false, unique = true, length = 100)
    private String domain;

    @Column(name = "current_players")
    @Builder.Default // // 빌더 사용 시 기본값 0 적용
    private Integer currentPlayers = 0;

    @Column(name = "max_players")
    @Builder.Default // // 빌더 사용 시 기본값 0 적용
    private Integer maxPlayers = 0;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ServerCategoryEntity> serverCategories = new LinkedHashSet<>();


    // 1. 카테고리 추가 메서드
    public void addCategory(CategoryEntity category) {
        ServerCategoryEntity serverCategory = ServerCategoryEntity.builder()
                .server(this)
                .category(category)
                .build();
        this.serverCategories.add(serverCategory);
    }

    // 2. 카테고리 전체 비우기 메서드
    public void clearCategories() {
        this.serverCategories.clear();
    }

    // 3. 기존 카테고리 업데이트
    public void updateCategories(Set<ServerCategoryEntity> newCategories) {
        this.serverCategories.clear();
        if (newCategories != null) {
            this.serverCategories.addAll(newCategories);
        }
    }

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ServerImageEntity> serverImageEntities = new LinkedHashSet<>();

    // 4. 이미지 추가 편의 메서드
    public void addImage(ServerImageEntity image) {
        this.serverImageEntities.add(image);
        if (image.getServer() != this) {
            image.mappingServer(this);
        }
    }

    // 5. 서버 상태를 업데이트하는 메서드
    public void updateStatus(ServerStatus status) {
        this.status = status;
    }

    // 6. 인원수 정보를 한꺼번에 업데이트하는 메서드
    public void updatePlayerCount(Integer currentPlayers, Integer maxPlayers) {
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.lastCheckedAt = java.time.LocalDateTime.now();
    }
}