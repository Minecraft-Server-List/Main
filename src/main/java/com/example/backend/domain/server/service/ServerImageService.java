package com.example.backend.domain.server.service;

import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.entity.ServerImageEntity;
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
    @Transactional
    public void uploadImages(Long serverId, List<MultipartFile> files) throws IOException {
        ServerEntity serverEntity = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));

        for (MultipartFile file : files) {
            String s3Url = s3Service.uploadImage(file);
            String s3Key = s3Service.extractKeyFromUrl(s3Url);

            ServerImageEntity imageEntity = ServerImageEntity.builder()
                    .server(serverEntity)
                    .imageUrl(s3Url)
                    .s3Key(s3Key)
                    .originalName(file.getOriginalFilename())
                    .build();

            // 1. 서버 엔티티의 리스트에 이미지를 직접 추가 (메모리 동기화)
            serverEntity.addImage(imageEntity);

            // 2. 이미지 데이터 저장
            serverImageRepository.save(imageEntity);
        }

        // 3. 변경된 서버 엔티티 상태를 DB에 즉시 반영
        serverRepository.saveAndFlush(serverEntity);
    }

    // 2. 이미지 삭제 기능
    public void deleteImage(Long imageId) {
        ServerImageEntity image = serverImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지 정보를 찾을 수 없습니다."));

        // 1. S3 창고에서 파일 삭제
        s3Service.deleteImage(image.getImageUrl());

        // 2. DB 레코드 삭제
        serverImageRepository.delete(image);
    }
}