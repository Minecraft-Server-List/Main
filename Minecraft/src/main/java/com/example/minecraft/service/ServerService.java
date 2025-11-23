package com.example.minecraft.service;

import com.example.minecraft.dao.ServerDAO;
import com.example.minecraft.dto.ServerDTO;

public class ServerService {

    private final ServerDAO serverDAO = new ServerDAO();

    // 1. 서버 생성
    public boolean createServerService(ServerDTO server) {

        // 유효성 검사
        if (server.getName().isEmpty()) return false;

        int result = serverDAO.createServer(server);
        return result == 1; // 리턴값이 1이면 서버가 생성되었다는 뜻 * ServerDAO 참조

    }

}
