package com.example.minecraft.service;

import com.example.minecraft.dao.ServerDAO;
import com.example.minecraft.dao.ServerImageDAO;
import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.dto.ServerImageDTO;
import com.example.minecraft.dto.ServerStatusDTO;
import com.example.minecraft.util.JdbcConnectUtil;
import com.example.minecraft.util.McStatusApiClient;
import com.google.gson.Gson;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import jakarta.servlet.http.Part;

public class ServerService {

    private static final String API_BASE_URL = "https://api.mcstatus.io/v2/status/java/";

    private final ServerDAO serverDAO = new ServerDAO();
    private final ServerImageDAO serverImageDAO = new ServerImageDAO();
    private final McStatusApiClient apiClient = new McStatusApiClient();
    private final Gson gson = new Gson();

    // 1. 서버 생성
    public boolean createServerService(ServerDTO serverDTO, Part imagePart, jakarta.servlet.ServletContext context) { // 🚨 Part 객체 추가

        // 유효성 검사 (생략)
        if (serverDTO.getName().isEmpty()) return false;

        Connection con = null;
        File uploadedFile = null; // 롤백 시 삭제할 파일 참조
        long newServerId = -1;

        try {
            // 1. 트랜잭션 시작 및 Connection 획득
            con = JdbcConnectUtil.getConnection();
            con.setAutoCommit(false);

            String UPLOAD_PATH = context.getRealPath("/") + "upload" + File.separator + "server_images";
            // 2. SERVER 테이블에 기본 정보 삽입 및 ID 획득
            newServerId = serverDAO.createServer(con, serverDTO); // Connection 전달

            // 3. 이미지 파일 처리 (Part가 유효한 경우)
            if (imagePart != null && imagePart.getSize() > 0) {

                String originalName = imagePart.getSubmittedFileName();
                String fileExtension = originalName.substring(originalName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension; // 고유 파일명 생성

                // 파일 시스템 경로 설정
                File uploadPath = new File(UPLOAD_PATH);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                uploadedFile = new File(uploadPath, newFileName); // 롤백 시 삭제할 파일 객체 생성
                imagePart.write(uploadedFile.getAbsolutePath()); // 파일 시스템에 저장

                // 4. SERVER_IMAGE 테이블에 이미지 정보 삽입
                ServerImageDTO imageDTO = new ServerImageDTO();
                imageDTO.setServerId(newServerId); // 획득한 ID 사용
                imageDTO.setFileName(newFileName);
                imageDTO.setOriginalName(originalName);
                imageDTO.setFilePath("upload/server_images");
                imageDTO.setFileSize(imagePart.getSize());

                serverImageDAO.createServerImage(con, imageDTO);
            }

            // 5. 성공 시 커밋
            con.commit();
            return true;

        } catch (Exception e) {
            // 6. 실패 시 롤백 및 파일 삭제 (필수)
            try {
                if (con != null) con.rollback();
                if (uploadedFile != null && uploadedFile.exists()) {
                    uploadedFile.delete(); // 저장된 파일도 삭제
                }
            } catch (Exception rollbackEx) {
                // 롤백 실패 시 로그
                System.err.println("트랜잭션 롤백 실패: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("서버 등록 트랜잭션 실패: " + e.getMessage(), e);

        } finally {
            // 7. 자원 반환
            JdbcConnectUtil.close(con);
        }

    }

    // 2-1. 서버 목록 조회
    public ArrayList<ServerDTO> getServerListService() {

        // 1. 서버 기본 목록 조회 (이미지/카테고리 정보는 DAO에서 JOIN으로 가져와야 함)
        ArrayList<ServerDTO> serverList = serverDAO.getServerList();

        // 2. 각 서버별 상태 정보 조회 및 설정
        for (ServerDTO server : serverList) {
            // 🚨 API 호출 로직만 남깁니다.
            String apiUrl = API_BASE_URL + server.getDomain();
            String jsonResponse = apiClient.callApi(apiUrl);

            ServerStatusDTO statusDTO;
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                statusDTO = gson.fromJson(jsonResponse, ServerStatusDTO.class);
            } else {
                statusDTO = new ServerStatusDTO();
                statusDTO.setOnline(false);
            }
            server.setServerStatus(statusDTO);
        }
        return serverList;
    }

    // 2-2. 서버 단일 조회
    public ServerDTO getServerByIdService(Long serverId) {

        // 1. 서버 기본 정보 조회 (이미지/카테고리 정보는 DAO에서 JOIN으로 가져와야 함)
        ServerDTO serverDTO = serverDAO.getServerById(serverId);

        if (serverDTO == null) {
            throw new NoSuchElementException("ID " + serverId + "에 해당하는 서버를 찾을 수 없습니다.");
        }

        // 🚨 API 호출 로직만 남깁니다.
        String apiUrl = API_BASE_URL + serverDTO.getDomain();
        String jsonResponse = apiClient.callApi(apiUrl);

        ServerStatusDTO statusDTO;
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            statusDTO = gson.fromJson(jsonResponse, ServerStatusDTO.class);
        } else {
            statusDTO = new ServerStatusDTO();
            statusDTO.setOnline(false);
        }
        serverDTO.setServerStatus(statusDTO);

        return serverDTO;
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

    // 5. 서버 검색
    public ArrayList<ServerDTO> searchServersService(String query) {
        // 쿼리가 유효한지 확인 후 DAO 호출
        if (query == null || query.trim().isEmpty()) {
            // 검색어가 없으면 전체 목록 반환
            return serverDAO.getServerList();
        }
        return serverDAO.searchServers(query);

    }

}
