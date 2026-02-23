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
        // 1. 파일이 비어있는지 간단 체크
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String imageUrl = s3Service.uploadImage(file);

        // 2. 공통 응답 포맷에 담아 리턴
        return ResponseEntity.ok(ApiResponse.success(200, imageUrl));
    }

    // 2. 이미지 삭제 API
    @DeleteMapping("/delete")
    // 1. 리턴 타입을 ResponseEntity<ApiResponse<String>>으로 수정 (에러 해결 포인트)
    public ResponseEntity<ApiResponse<String>> deleteImage(@RequestParam("url") String imageUrl) {

        // 2. S3Service의 삭제 로직 호출
        s3Service.deleteImage(imageUrl);

        // 3. ApiResponse.success가 반환하는 타입과 위 선언 타입을 일치시킴
        return ResponseEntity.ok(ApiResponse.success(200, imageUrl));
    }
}