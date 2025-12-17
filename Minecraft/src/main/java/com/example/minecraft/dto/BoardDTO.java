package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class BoardDTO {
    private Long baseBoardId;
    private Long userId;
    
    // [핵심 변경] DB의 board_category 테이블 매핑
    private Long categoryId;     // DB PK (board_category_id)
    private String category;     // DB code (NOTICE, FREE...) - 로직 제어용
    private String categoryName; // DB name (공지사항, 자유게시판...) - 화면 출력용

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private int viewCount;     
    private int likeCount;     

    private String writerName; 
    private boolean isLiked;   

    // --- Getters and Setters ---
    public Long getBaseBoardId() { return baseBoardId; }
    public void setBaseBoardId(Long baseBoardId) { this.baseBoardId = baseBoardId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

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