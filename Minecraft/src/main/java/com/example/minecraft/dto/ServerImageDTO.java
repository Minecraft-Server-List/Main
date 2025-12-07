package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class ServerImageDTO {

    private Long imageId;
    private Long serverId;        // 1:1 관계를 위한 FK
    private String fileName;      // UUID로 변환된 파일명
    private String originalName;  // 사용자가 업로드한 원본 파일명
    private String filePath;      // 서버 디스크 저장 경로
    private Long fileSize;
    private LocalDateTime uploadedAt;

    // 생성자

    public ServerImageDTO() {
    }

    // getter 및 setter

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}