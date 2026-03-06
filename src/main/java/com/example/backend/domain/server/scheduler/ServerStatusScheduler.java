package com.example.backend.domain.server.scheduler;

import com.example.backend.domain.server.dto.MinecraftServerStatus;
import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.repository.ServerRepository;
import com.example.backend.domain.server.type.ServerStatus; // // ENUM 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerStatusScheduler {

    private final ServerRepository serverRepository;
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void updateAllServersStatus() {
        log.info("--- 마인크래프트 서버 상태 동기화 시작 ---");

        List<ServerEntity> serverEntities = serverRepository.findAll();

        for (ServerEntity serverEntity : serverEntities) {
            try {
                String apiUrl = "https://api.mcstatus.io/v2/status/java/" + serverEntity.getDomain();
                MinecraftServerStatus status = restTemplate.getForObject(apiUrl, MinecraftServerStatus.class);

                if (status != null && status.isOnline()) {
                    // // 1. 서버가 온라인인 경우: 정보 업데이트
                    serverEntity.updateStatusAndPlayers(
                            ServerStatus.ONLINE,
                            status.getPlayers() != null ? status.getPlayers().getOnline() : 0,
                            status.getPlayers() != null ? status.getPlayers().getMax() : 0
                    );
                    log.info("온라인: {} ({} / {})", serverEntity.getDomain(), serverEntity.getCurrentPlayers(), serverEntity.getMaxPlayers());
                } else {
                    // // 2. 서버가 오프라인인 경우 (status가 null이거나 isOnline이 false인 경우)
                    serverEntity.updateStatusAndPlayers(ServerStatus.OFFLINE, 0, 0);
                    log.info("오프라인: {} (인원수 초기화 완료)", serverEntity.getDomain());
                }
            } catch (Exception e) {
                // // 3. API 호출 에러 발생 시: 안전하게 오프라인 및 0으로 처리
                serverEntity.updateStatusAndPlayers(ServerStatus.OFFLINE, 0, 0);
                log.error("조회 실패: {} - {}", serverEntity.getDomain(), e.getMessage());
            }
        }
        log.info("--- 서버 상태 동기화 완료 ---");
    }
}