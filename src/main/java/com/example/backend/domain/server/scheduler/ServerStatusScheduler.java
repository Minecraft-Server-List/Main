package com.example.backend.domain.server.scheduler;

import com.example.backend.domain.server.dto.MinecraftServerStatus;
import com.example.backend.domain.server.entity.ServerEntity;
import com.example.backend.domain.server.repository.ServerRepository;
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
    //@Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void updateAllServersStatus() {
        log.info("--- 마인크래프트 서버 상태 동기화 시작 ---");

        List<ServerEntity> servers = serverRepository.findAll();

        for (ServerEntity server : servers) {
            try {
                String apiUrl = "https://api.mcstatus.io/v2/status/java/" + server.getDomain();

                MinecraftServerStatus status = restTemplate.getForObject(apiUrl, MinecraftServerStatus.class);

                if (status != null) {
                    server.updateStatus(
                            status.isOnline() ? "ONLINE" : "OFFLINE",
                            status.isOnline() ? status.getPlayerInfo().getCurrentCount() : 0,
                            status.isOnline() ? status.getPlayerInfo().getMaxCount() : 0
                    );
                    log.info("성공: {} -> {}", server.getDomain(), status.isOnline() ? "ONLINE" : "OFFLINE");
                }
            } catch (Exception e) {
                log.error("에러: {} 서버 조회 실패 - {}", server.getDomain(), e.getMessage());
            }
        }
        log.info("--- 서버 상태 동기화 완료 ---");
    }
}