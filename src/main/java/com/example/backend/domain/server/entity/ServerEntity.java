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
    @Builder.Default
    private ServerStatus status = ServerStatus.OFFLINE;

    @Column(length = 20)
    private String version;

    @Column(nullable = false, unique = true, length = 100)
    private String domain;

    @Column(name = "current_players")
    @Builder.Default
    private Integer currentPlayers = 0;

    @Column(name = "max_players")
    @Builder.Default
    private Integer maxPlayers = 0;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    // 1. 카테고리와의 연관 관계 (중간 테이블 사용)
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ServerCategoryEntity> serverCategories = new LinkedHashSet<>();

    // 2. 이미지와의 연관 관계
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ServerImageEntity> serverImageEntities = new LinkedHashSet<>();

    // 1. 서버 상태 업데이트
    public void updateStatus(ServerStatus status) {
        this.status = status;
    }

    // 2. 카테고리 추가
    public void addCategory(CategoryEntity category) {
        ServerCategoryEntity serverCategory = ServerCategoryEntity.builder()
                .server(this)
                .category(category)
                .build();
        this.serverCategories.add(serverCategory);
    }

    // 3. 카테고리 초기화 (수정 시 사용)
    public void clearCategories() {
        this.serverCategories.clear();
    }

    // 4. 이미지 추가
    public void addImage(ServerImageEntity image) {
        this.serverImageEntities.add(image);
        if (image.getServer() != this) {
            image.mappingServer(this);
        }
    }

    // 5. 플레이어 정보 동시 업데이트 (스케줄러용)
    public void updateStatusAndPlayers(ServerStatus status, Integer current, Integer max) {
        this.status = status;
        this.currentPlayers = (current != null) ? current : 0;
        this.maxPlayers = (max != null) ? max : 0;
        this.lastCheckedAt = LocalDateTime.now(); // // 체크할 때마다 무조건 갱신
    }
}