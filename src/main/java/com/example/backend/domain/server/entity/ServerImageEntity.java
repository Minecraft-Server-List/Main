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

    // 1. 서버 엔티티와 N:1 연관 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerEntity server;

    // 2. S3 관리용 키 (UUID.확장자 형태 - 삭제 시 활용)
    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    // 3. CloudFront 조회용 URL (보여주기용)
    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    // 4. 사용자가 올린 원래 파일명
    private String originalName;

    // 5. 업로드 시간
    @CreatedDate
    private LocalDateTime uploadedAt;

    // 6. 서버 엔티티와의 연관 관계 편의 메서드
    public void setServer(ServerEntity server) {
        this.server = server;
    }
}