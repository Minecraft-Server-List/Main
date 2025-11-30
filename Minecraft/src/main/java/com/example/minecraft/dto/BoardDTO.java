package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class BoardDTO {
    // DB 컬럼 매핑 필드
    private Long baseBoardId;
    private Long userId;
    private String category;   // [신규] 게시판 카테고리 (NOTICE, FREE, QNA 등)
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 조회수 & 좋아요 수
    private int viewCount;     
    private int likeCount;     

    // 화면 표시용 추가 필드
    private String writerName; 
    private boolean isLiked;   

    // --- Getters and Setters ---
    public Long getBaseBoardId() { return baseBoardId; }
    public void setBaseBoardId(Long baseBoardId) { this.baseBoardId = baseBoardId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // [신규] 카테고리 Getter/Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public String getWriterName() { return writerName; }
    public void setWriterName(String writerName) { this.writerName = writerName; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean isLiked) { this.isLiked = isLiked; }
}