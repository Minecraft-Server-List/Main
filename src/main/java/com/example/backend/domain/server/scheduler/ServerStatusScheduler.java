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

    // // 테스트를 위해 10초(10000ms) 설정 유지, 실배포 시에는 cron 사용 권장
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void updateAllServersStatus() {
        log.info("--- 마인크래프트 서버 상태 동기화 시작 ---");

        // // @EntityGraph가 적용된 findAll()을 사용하여 N+1 문제 방지 (리팩토링한 레포지토리 활용)
        List<ServerEntity> serverEntities = serverRepository.findAll();

        for (ServerEntity serverEntity : serverEntities) {
            try {
                String apiUrl = "https://api.mcstatus.io/v2/status/java/" + serverEntity.getDomain();
                MinecraftServerStatus status = restTemplate.getForObject(apiUrl, MinecraftServerStatus.class);

                if (status != null) {
                    // // 1. 문자열 대신 ENUM 상수를 전달하여 타입 에러 해결
                    ServerStatus nextStatus = status.isOnline() ? ServerStatus.ONLINE : ServerStatus.OFFLINE;

                    // // 2. 엔티티에 추가한 비즈니스 메서드 활용
                    serverEntity.updateStatus(nextStatus);

                    // // 3. 인원수 정보도 함께 업데이트 (있을 경우)
                    if (status.getPlayers() != null) {
                        serverEntity.setCurrentPlayers(status.getPlayers().getOnline());
                        serverEntity.setMaxPlayers(status.getPlayers().getMax());
                    }

                    log.info("성공: {} -> {}", serverEntity.getDomain(), nextStatus);
                }
            } catch (Exception e) {
                // // 서버 조회 실패 시 OFFLINE으로 안전하게 변경
                serverEntity.updateStatus(ServerStatus.OFFLINE);
                log.error("에러: {} 서버 조회 실패 - {}", serverEntity.getDomain(), e.getMessage());
            }
        }

        // // @Transactional 덕분에 반복문 종료 후 변경 감지(Dirty Checking)로 자동 업데이트됩니다.
        log.info("--- 서버 상태 동기화 완료 ---");
    }
}