package com.example.backend.domain.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "server_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ServerImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private ServerEntity server;

    private String fileName;     // 서버에 저장된 고유 파일명 (UUID_원본파일명)
    private String originalName; // 유저가 올린 실제 파일명
    private String filePath;     // 저장 경로
    private Long fileSize;
    private LocalDateTime uploadedAt;
}