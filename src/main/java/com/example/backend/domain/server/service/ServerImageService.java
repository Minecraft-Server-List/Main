package com.example.backend.domain.server.service;

import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.entity.ServerImageEntity;
import com.example.backend.domain.server.repository.ServerImageRepository;
import com.example.backend.domain.server.repository.ServerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServerImageService {

    private final ServerImageRepository serverImageRepository;
    private final ServerRepository serverRepository;

    private final String uploadPath = "/Users/yusiyeong/mcreview/uploads/";

    public void uploadImages(Long serverId, List<MultipartFile> files) throws IOException {
        ServerEntity server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("서버를 찾을 수 없습니다."));

        File folder = new File(uploadPath);
        if (!folder.exists()) folder.mkdirs(); // 폴더 없으면 생성

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalName = file.getOriginalFilename();

            // 확장자 추출 (.png, .jpg 등)
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // 파일명은 오직 UUID와 확장자로만 구성 (한글/공백 제거)
            String fileName = UUID.randomUUID().toString() + extension;
            String fullPath = uploadPath + fileName;

            // 1. 실제 파일 저장
            file.transferTo(new File(fullPath));

            // 2. DB에 정보 저장
            ServerImageEntity image = ServerImageEntity.builder()
                    .server(server)
                    .originalName(originalName) // 원본 이름은 DB에만 보관 (참고용)
                    .fileName(fileName)         // 실제 파일명은 깔끔한 UUID (접근용)
                    .filePath(fullPath)
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            serverImageRepository.save(image);
        }
    }
}
