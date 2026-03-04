package com.example.backend.domain.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "server_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ServerImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    // N:1 연관 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerEntity server;

    // S3 내 식별자 (UUID 등)
    @Column(name = "s3_key", nullable = false, length = 255)
    private String s3Key;

    // CloudFront 또는 S3 접근 주소
    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    // 원본 파일명
    @Column(name = "original_name", length = 512)
    private String originalName;

    // 업로드 시간 (Auditing 기능 활용)
    @CreatedDate
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    // 서버 엔티티 설정 시 양방향 관계를 안전하게 처리
    public void mappingServer(ServerEntity server) {
        this.server = server;
        // 서버 엔티티의 이미지 리스트에도 자기 자신을 추가
        if (!server.getServerImageEntities().contains(this)) {
            server.getServerImageEntities().add(this);
        }
    }
}