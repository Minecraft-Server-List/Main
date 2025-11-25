package com.example.minecraft.dto;

import java.time.LocalDateTime;

public class ComentDTO {
    private Long comentId;
    private Long userId;
    private Long baseBoardId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // 수정일
    
    // [JOIN 및 서브쿼리 결과 필드]
    private String writerName; // 작성자 이름
    private String boardTitle; // 원본 글 제목
    
    // [좋아요 관련 수정]
    private int likeCount;      // 해당 댓글의 총 좋아요 수
    private boolean isLiked;    // 현재 로그인한 유저가 좋아요를 눌렀는지 여부

    // --- Getters and Setters ---
    public Long getComentId() { return comentId; }
    public void setComentId(Long comentId) { this.comentId = comentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBaseBoardId() { return baseBoardId; }
    public void setBaseBoardId(Long baseBoardId) { this.baseBoardId = baseBoardId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getWriterName() { return writerName; }
    public void setWriterName(String writerName) { this.writerName = writerName; }
    public String getBoardTitle() { return boardTitle; }
    public void setBoardTitle(String boardTitle) { this.boardTitle = boardTitle; }
    
    // 좋아요 Getter/Setter
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean isLiked) { this.isLiked = isLiked; }
}