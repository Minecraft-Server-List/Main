package com.example.backend.domain.server.controller;

import com.example.backend.domain.server.dto.ServerRequestDto;
import com.example.backend.domain.server.dto.ServerResponseDto;
import com.example.backend.domain.server.service.ServerImageService;
import com.example.backend.domain.server.service.ServerService;
import com.example.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // 1. 서버 통합 등록 (POST)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ServerResponseDto>> registerServer(
            @RequestPart("server") ServerRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        // 서버 기본 정보 및 카테고리 저장
        ServerResponseDto response = serverService.createServer(requestDto);

        // 파일이 있을 경우 이미지 업로드 및 DB 연동
        if (files != null && !files.isEmpty()) {
            serverImageService.uploadImages(response.getServerId(), files);
        }

        // 최신 상태 정보(이미지 포함) 다시 조회 후 반환
        ServerResponseDto finalResponse = serverService.getServer(response.getServerId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, finalResponse));
    }

    // 2. 전체 서버 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServerResponseDto>>> getAllServers() {
        List<ServerResponseDto> response = serverService.getAllServers();
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 3. 특정 서버 상세 조회 (GET)
    @GetMapping("/{serverId}")
    public ResponseEntity<ApiResponse<ServerResponseDto>> getServer(@PathVariable Long serverId) {
        ServerResponseDto response = serverService.getServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 4. 서버 정보 수정 (PATCH)
    @PatchMapping(value = "/{serverId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ServerResponseDto>> patchServer(
            @PathVariable Long serverId,
            @RequestPart(value = "server", required = false) ServerRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        // 정보 수정이 요청된 경우에만 실행 (ENUM 상태 포함)
        if (requestDto != null) {
            serverService.updateServer(serverId, requestDto);
        }

        // 파일이 넘어온 경우에만 추가 업로드 실행
        if (files != null && !files.isEmpty()) {
            serverImageService.uploadImages(serverId, files);
        }

        // 최종 결과(수정된 데이터 + 추가된 이미지) 조회 후 반환
        ServerResponseDto finalResponse = serverService.getServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, finalResponse));
    }

    // 5. 서버 삭제 (DELETE)
    @DeleteMapping("/{serverId}")
    public ResponseEntity<ApiResponse<String>> deleteServer(@PathVariable Long serverId) {
        serverService.deleteServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, "서버 ID: " + serverId + " 삭제가 완료되었습니다."));
    }

    // 6. 이미지 개별 삭제 (DELETE)
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<String>> deleteImage(@PathVariable Long imageId) {
        serverImageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success(200, "이미지 ID: " + imageId + " 삭제가 완료되었습니다."));
    }
}