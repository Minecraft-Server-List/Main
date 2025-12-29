package com.example.backend.domain.user.dto;

import com.example.backend.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private String name;
    private String email;
    private String password;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password) // 나중에는 여기에 암호화를 걸 거예요!
                .role("USER") // 기본 권한 설정
                .build();
    }
}