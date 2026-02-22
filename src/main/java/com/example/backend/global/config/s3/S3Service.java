package com.example.backend.global.config.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket; // 1. yusiyeong-media-bucket-2026 주입

    // 2. 이미지 업로드 처리
    public String uploadImage(MultipartFile file) {
        // 3. 파일명 중복 방지를 위한 랜덤 이름 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // 4. S3로 파일 스트림 전송
            s3Template.upload(bucket, fileName, file.getInputStream());
        } catch (IOException e) {
            // 5. 예외 발생 시 런타임 예외로 던짐 (GlobalExceptionHandler가 처리)
            throw new IllegalArgumentException("이미지 업로드 중 오류가 발생했습니다.");
        }

        // 6. 퍼블릭 읽기가 가능한 URL 반환
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, fileName);
    }
}