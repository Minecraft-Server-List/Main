package com.example.backend.domain.server.controller;

import com.example.backend.domain.server.dto.ServerRequestDto;
import com.example.backend.domain.server.dto.ServerResponseDto;
import com.example.backend.domain.server.service.ServerImageService;
import com.example.backend.domain.server.service.ServerService;
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

    @PostMapping
    public ResponseEntity<ServerResponseDto> createServer(@RequestBody ServerRequestDto requestDto) {
        return ResponseEntity.ok(serverService.createServer(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<ServerResponseDto>> getAllServers() {
        return ResponseEntity.ok(serverService.getAllServers());
    }

    @GetMapping("/{serverId}")
    public ResponseEntity<ServerResponseDto> getServer(@PathVariable Long serverId) {
        return ResponseEntity.ok(serverService.getServer(serverId));
    }

    @PutMapping("/{serverId}")
    public ResponseEntity<ServerResponseDto> updateServer(@PathVariable Long serverId, @RequestBody ServerRequestDto requestDto) {
        return ResponseEntity.ok(serverService.updateServer(serverId, requestDto));
    }

    @DeleteMapping("/{serverId}")
    public ResponseEntity<Void> deleteServer(@PathVariable Long serverId) {
        serverService.deleteServer(serverId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{serverId}/images")
    public ResponseEntity<String> uploadImages(
            @PathVariable Long serverId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        serverImageService.uploadImages(serverId, files);
        return ResponseEntity.ok("이미지 업로드 성공!");
    }
}
