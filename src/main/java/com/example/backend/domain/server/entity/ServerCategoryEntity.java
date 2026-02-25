package com.example.backend.domain.server.entity;

import com.example.backend.domain.category.entity.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "server_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(ServerCategoryIdEntity.class) // 복합키 클래스 연결
public class ServerCategoryEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private ServerEntity serverEntity;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
}
