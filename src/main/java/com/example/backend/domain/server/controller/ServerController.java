package com.example.backend.domain.server.controller;

import com.example.backend.domain.server.dto.ServerRequestDto;
import com.example.backend.domain.server.dto.ServerResponseDto;
import com.example.backend.domain.server.service.ServerImageService;
import com.example.backend.domain.server.service.ServerService;
import com.example.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final ServerImageService serverImageService;

    // 1. 서버 통합 등록
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ServerResponseDto>> registerServer(
            @RequestPart("server") ServerRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        // 2. 서버 기본 정보 및 카테고리 저장
        ServerResponseDto response = serverService.createServer(requestDto);

        // 3. 파일이 있을 경우 S3 업로드 연동
        if (files != null && !files.isEmpty()) {
            serverImageService.uploadImages(response.getServerId(), files);
        }

        // 4. 최신 상태 정보 다시 조회 후 반환
        ServerResponseDto finalResponse = serverService.getServer(response.getServerId());
        return ResponseEntity.ok(ApiResponse.success(201, finalResponse));
    }

    // 5. 전체 서버 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServerResponseDto>>> getAllServers() {
        List<ServerResponseDto> response = serverService.getAllServers();
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 6. 특정 서버 상세 조회
    @GetMapping("/{serverId}")
    public ResponseEntity<ApiResponse<ServerResponseDto>> getServer(@PathVariable Long serverId) {
        ServerResponseDto response = serverService.getServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 7. 서버 정보 수정
    @PutMapping("/{serverId}")
    public ResponseEntity<ApiResponse<ServerResponseDto>> updateServer(
            @PathVariable Long serverId,
            @RequestBody ServerRequestDto requestDto) {
        ServerResponseDto response = serverService.updateServer(serverId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 8. 서버 삭제
    @DeleteMapping("/{serverId}")
    public ResponseEntity<ApiResponse<Void>> deleteServer(@PathVariable Long serverId) {
        serverService.deleteServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }

    // 9. 이미지 개별 삭제
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        serverImageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }
}