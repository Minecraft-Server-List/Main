package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class BoardDTO {
    private Long baseBoardId;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // [편의상 추가] DB 컬럼에는 없지만 화면에 작성자 이름을 띄우기 위해 추가
    private String writerName;

    // Getters and Setters
    public Long getBaseBoardId() { return baseBoardId; }
    public void setBaseBoardId(Long baseBoardId) { this.baseBoardId = baseBoardId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getWriterName() { return writerName; }
    public void setWriterName(String writerName) { this.writerName = writerName; }
}