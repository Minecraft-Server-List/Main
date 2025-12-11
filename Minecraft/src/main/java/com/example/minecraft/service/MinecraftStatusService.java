package com.example.minecraft.service;

import com.example.minecraft.dao.ServerDAO;
import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.dto.ServerStatusDTO;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MinecraftStatusService {

    private final ServerDAO serverDAO = new ServerDAO();
    private final Gson gson = new Gson();

    public void updateAllServerStatuses() {
        // 1. DB에서 모든 서버 목록 (ID, Domain) 가져오기
        ArrayList<ServerDTO> servers = serverDAO.getAllServerDomains();

        for (ServerDTO server : servers) {
            String domain = server.getDomain();
            // 2. 외부 API 호출 및 상태 확인
            ServerStatusDTO status = fetchServerStatus(domain);

            // 3. DB 업데이트에 필요한 값 준비
            String statusString;
            int onlinePlayers;
            int maxPlayers;

            if (status.online) {
                statusString = "ACTIVE";
                onlinePlayers = status.players != null ? status.players.online : 0;
                maxPlayers = status.players != null ? status.players.max : 0;
            } else {
                statusString = "OFFLINE";
                onlinePlayers = 0;
                maxPlayers = 0;
            }

            // 🚨 4. 수정: 새로 만든 updateServerStatus 메서드 호출
            serverDAO.updateServerStatus(
                    server.getServerId(), // 🚨 Server ID 추가
                    onlinePlayers,
                    maxPlayers,
                    statusString
            );
        }
    }

    // 🚨 외부 API 호출 로직 (HttpURLConnection 또는 HttpClient 사용)
    private ServerStatusDTO fetchServerStatus(String domain) {
        // 예시 API URL: https://api.minehut.com/server/${domain}/status
        // 실제 마인크래프트 서버 상태를 조회하는 외부 API URL로 변경해야 합니다.
        String apiUrl = "https://api.mcstatus.io/v2/status/java/" + domain;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5초 타임아웃

            // ... 응답 처리 및 JSON 파싱 로직 ...
            // (응답이 200 OK일 경우에만 파싱)

            ServerStatusDTO status = gson.fromJson(new InputStreamReader(conn.getInputStream()), ServerStatusDTO.class);
            return status;

        } catch (Exception e) {
            // API 호출 실패 (타임아웃, 404 등) 시 OFFINE 객체 반환
            ServerStatusDTO offlineStatus = new ServerStatusDTO();
            offlineStatus.online = false;
            return offlineStatus;
        }
    }
}
