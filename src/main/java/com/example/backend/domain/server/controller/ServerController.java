package com.example.backend.domain.server.controller;

import com.example.backend.domain.server.dto.ServerRequestDto;
import com.example.backend.domain.server.dto.ServerResponseDto;
import com.example.backend.domain.server.service.ServerImageService;
import com.example.backend.domain.server.service.ServerService;
import com.example.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
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

    // 1. 서버 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ServerResponseDto>> createServer(@RequestBody ServerRequestDto requestDto) {
        ServerResponseDto response = serverService.createServer(requestDto);
        return ResponseEntity.ok(ApiResponse.success(201, response));
    }

    // 2. 전체 서버 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServerResponseDto>>> getAllServers() {
        List<ServerResponseDto> response = serverService.getAllServers();
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 3. 특정 서버 상세 조회
    @GetMapping("/{serverId}")
    public ResponseEntity<ApiResponse<ServerResponseDto>> getServer(@PathVariable Long serverId) {
        ServerResponseDto response = serverService.getServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 4. 서버 정보 수정
    @PutMapping("/{serverId}")
    public ResponseEntity<ApiResponse<ServerResponseDto>> updateServer(
            @PathVariable Long serverId,
            @RequestBody ServerRequestDto requestDto) {
        ServerResponseDto response = serverService.updateServer(serverId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    // 5. 서버 삭제 (S3 이미지도 함께 삭제되도록 연동 예정)
    @DeleteMapping("/{serverId}")
    public ResponseEntity<ApiResponse<Void>> deleteServer(@PathVariable Long serverId) {
        serverService.deleteServer(serverId);
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }

    // 6. 서버 이미지 다중 업로드 (S3 연동 방식)
    @PostMapping("/{serverId}/images")
    public ResponseEntity<ApiResponse<String>> uploadImages(
            @PathVariable Long serverId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        serverImageService.uploadImages(serverId, files);
        return ResponseEntity.ok(ApiResponse.success(200, "이미지 업로드 성공!"));
    }
}