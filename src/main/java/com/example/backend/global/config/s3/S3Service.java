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

    // 1. S3 버킷 이름 주입
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 2. CloudFront 도메인 주입
    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    // 1. 이미지 업로드 처리 로직
    public String uploadImage(MultipartFile file) {
        // 1. 확장자 추출 (파일명 은닉을 위해 필요)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 2. 원본 파일명을 완전히 숨긴 새로운 랜덤 파일명 생성
        String fileName = UUID.randomUUID().toString() + extension;

        // 3. 혹시 모를 설정값 공백 제거
        String cleanBucketName = bucket.trim();

        // 4. 로그 확인
        System.out.println("업로드 시도 버킷: [" + cleanBucketName + "]");
        System.out.println("생성된 파일명: " + fileName);

        try {
            // 5. S3로 파일 스트림 전송
            s3Template.upload(cleanBucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            // 6. 예외 발생 시 스택 트레이스 출력 및 예외 던짐
            e.printStackTrace();
            throw new IllegalArgumentException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 7. CloudFront 주소로 반환
        return String.format("https://%s/%s", cloudFrontDomain, fileName);
    }

    // 2. 이미지 삭제 처리 로직 (URL만 있을 때)
    public void deleteImage(String imageUrl) {
        // 1. URL에서 파일명(S3 Key)만 추출
        // 예: https://d123.cloudfront.net/abc-123.png -> abc-123.png
        String s3Key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        // 2. 설정값 공백 제거
        String cleanBucketName = bucket.trim();

        try {
            // 3. S3 창고에서 해당 키를 가진 파일 삭제
            s3Template.deleteObject(cleanBucketName, s3Key);
            System.out.println("S3 파일 삭제 성공: " + s3Key);
        } catch (Exception e) {
            // 4. 삭제 실패 시 스택 트레이스 출력 및 예외 던짐
            e.printStackTrace();
            throw new IllegalArgumentException("이미지 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 3. S3키 추출하는 메소드
    public String extractKeyFromUrl(String imageUrl) {
        // 1. CloudFront 주소나 S3 엔드포인트 이후의 파일명만 잘라냅니다.
        // ex) https://cloud-front.net/images/uuid.png -> images/uuid.png
        if (imageUrl == null || !imageUrl.contains(".net/")) {
            return imageUrl;
        }
        return imageUrl.substring(imageUrl.lastIndexOf(".net/") + 5);
    }
}