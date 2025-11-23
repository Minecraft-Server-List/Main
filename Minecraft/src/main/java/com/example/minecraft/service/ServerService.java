package com.example.minecraft.service;

import com.example.minecraft.dao.ServerDAO;
import com.example.minecraft.dto.ServerDTO;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ServerService {

    private final ServerDAO serverDAO = new ServerDAO();

    // 1. 서버 생성
    public boolean createServerService(ServerDTO server) {

        // 유효성 검사
        if (server.getName().isEmpty()) return false;

        int result = serverDAO.createServer(server);
        return result == 1; // 리턴값이 1이면 서버가 생성되었다는 뜻 * ServerDAO 참조

    }

    // 2-1. 서버 목록 조회
    public ArrayList<ServerDTO> getServerListService() {

        ArrayList<ServerDTO> list = serverDAO.getServerList();

        // 유효성 검사 필요하면 추가

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
