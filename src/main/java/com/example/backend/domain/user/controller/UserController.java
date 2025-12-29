package com.example.backend.domain.user.controller;

import com.example.backend.domain.user.dto.LoginRequestDto;
import com.example.backend.domain.user.dto.UserRequestDto;
import com.example.backend.domain.user.dto.UserResponseDto;
import com.example.backend.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // 브라우저 주소창에 http://localhost:8080/api/users 치면 나옴
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    // 1-1. 유저 회원가입
    @PostMapping("/signup")
    public UserResponseDto signup(@RequestBody UserRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    // 1-2. 유저 로그인
    @PostMapping("/login")
    public UserResponseDto login(@RequestBody LoginRequestDto requestDto) {
        return userService.login(requestDto);
    }

    // 2-1. 유저 전체 조회
    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getAllUsers(); // 객체 리스트를 반환하면 알아서 JSON으로 변환됨!
    }
}
