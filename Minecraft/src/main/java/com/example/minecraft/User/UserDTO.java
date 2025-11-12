package com.example.minecraft.User;

// DATETIME 타입 매핑을 위해 import
import java.time.LocalDateTime;

/**
 * users 테이블과 매핑되는 DTO (Data Transfer Object)
 * 클래스 이름을 userDTO -> UserDTO로 변경
 */
public class UserDTO {

    // DB의 user_id (BIGINT) 컬럼과 매핑
    // 타입: String -> Long, 이름: userid -> userId
    private Long userId;

    // DB의 password (VARCHAR) 컬럼과 매핑 (일치)
    private String password;

    // DB의 name (VARCHAR) 컬럼과 매핑 (일치)
    private String name;

    // DB의 email (VARCHAR) 컬럼과 매핑 (일치)
    private String email;

    // DB의 role (VARCHAR) 컬럼 추가
    private String role;

    // DB의 created_at (DATETIME) 컬럼 추가
    private LocalDateTime createdAt;

    
    // --- Getters and Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}