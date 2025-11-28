package com.example.minecraft.service;

import com.example.minecraft.dao.ServerDAO;
import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.dto.ServerStatusDTO;
import com.example.minecraft.util.McStatusApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ServerService {

    private static final String API_BASE_URL = "https://api.mcstatus.io/v2/status/java/";

    private final ServerDAO serverDAO = new ServerDAO();
    private final McStatusApiClient apiClient = new McStatusApiClient();
    private final Gson gson = new Gson();

    // 1. 서버 생성
    public boolean createServerService(ServerDTO server) {

        // 유효성 검사
        if (server.getName().isEmpty()) return false;

        int result = serverDAO.createServer(server);
        return result == 1; // 리턴값이 1이면 서버가 생성되었다는 뜻 * ServerDAO 참조

    }

    // 2-1. 서버 목록 조회
    // @return DB 정보 + API 상태 정보가 담긴 ServerDTO 목록
    public ArrayList<ServerDTO> getServerListService() {

        ArrayList<ServerDTO> list = serverDAO.getServerList();

        for (ServerDTO server : list) {
            String domain = server.getDomain();

            if (domain == null || domain.isEmpty()) {
                server.setStatus("Offline (No Host)");
                continue;
            }

            ServerStatusDTO statusDTO = apiClient.getStatusByDomain(domain);

            if (statusDTO.getOnline()) { // isOnline() Getter 호출 (올바른 방법)
                server.setStatus("Online");
                server.setServerStatus(statusDTO);

            } else {
                server.setStatus("Offline");
            }
        }

        return list;

    }

    // 2-2. 서버 단일 조회
    public ServerDTO getServerService(long id) {

        ServerDTO server = serverDAO.getServerById(id);

        if (server == null) {
            throw new NoSuchElementException("해당 ID(" + id + ")의 서버를 찾을 수 없습니다.");
        }

        return server;

    }

    // 3. 서버 수정
    public boolean updateServerService(ServerDTO server) {

        if (server.getName().isEmpty()) return false;
        int result = serverDAO.updateServer(server);
        return result == 1;

    }

    // 4. 서버 삭제
    public boolean deleteServerService(long id) {

        int result = serverDAO.deleteServerById(id);
        return result == 1;

    }

}
