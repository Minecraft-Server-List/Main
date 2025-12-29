package com.example.backend.domain.user.service;

import com.example.backend.domain.user.dto.LoginRequestDto;
import com.example.backend.domain.user.dto.UserRequestDto;
import com.example.backend.domain.user.dto.UserResponseDto;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 1-1. 유저 회원가입
    @Transactional
    public UserResponseDto signup(UserRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        UserEntity user = UserEntity.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .role("USER")
                .build();

        return UserResponseDto.from(userRepository.save(user));
    }

    // 1-2. 유저 로그인
    public UserResponseDto login(LoginRequestDto requestDto) {
        UserEntity user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDto.from(user);
    }

    // 2-1. 유저 전체 조회
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::from) // 훨씬 깔끔하죠?
                .toList();
    }
}