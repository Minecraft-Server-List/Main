package com.example.backend.domain.image.controller;

import com.example.backend.global.common.ApiResponse;
import com.example.backend.global.config.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    // 1. 이미지 단건 업로드 API
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        // 2. 파일이 비어있는지 간단 체크
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String imageUrl = s3Service.uploadImage(file);

        // 3. 공통 응답 포맷에 담아 리턴
        return ResponseEntity.ok(ApiResponse.success(200, imageUrl));
    }
}