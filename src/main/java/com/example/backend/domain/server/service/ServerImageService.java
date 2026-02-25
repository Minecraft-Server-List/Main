package com.example.backend.domain.server.service;

import com.example.backend.domain.server.entity.Server;
import com.example.backend.domain.server.entity.ServerImage;
import com.example.backend.domain.server.repository.ServerImageRepository;
import com.example.backend.domain.server.repository.ServerRepository;
import com.example.backend.global.config.s3.S3Service; // S3Service 임포트
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServerImageService {

    private final ServerImageRepository serverImageRepository;
    private final ServerRepository serverRepository;
    // 이미 작성해둔 S3Service 주입
    private final S3Service s3Service;

    // 1. 이미지 업로드 기능
    public void uploadImages(Long serverId, List<MultipartFile> files) throws IOException {
        // 2. 이미지를 등록할 서버 엔티티 조회
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // 3. S3Service를 통해 파일 업로드 및 CloudFront URL 획득
            // 이 메서드 내부에서 UUID 파일명 생성 및 확장자 처리가 이미 완료됨
            String imageUrl = s3Service.uploadImage(file);

            // 4. URL에서 s3Key(파일명)만 추출
            String s3Key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // 5. 변경된 엔티티 구조에 맞게 DB 정보 저장
            ServerImage image = ServerImage.builder()
                    .server(server)
                    .originalName(file.getOriginalFilename()) // 원본 파일명 보관
                    .s3Key(s3Key)                             // S3 관리용 키
                    .imageUrl(imageUrl)                       // CloudFront 조회용 URL
                    .build();

            serverImageRepository.save(image);
        }
    }

    // 2. 이미지 삭제 기능
    public void deleteImage(Long imageId) {
        ServerImage image = serverImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지 정보를 찾을 수 없습니다."));

        // 1. S3 창고에서 파일 삭제
        s3Service.deleteImage(image.getImageUrl());

        // 2. DB 레코드 삭제
        serverImageRepository.delete(image);
    }
}