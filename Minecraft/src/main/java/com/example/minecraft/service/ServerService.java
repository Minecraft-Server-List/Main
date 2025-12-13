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

    private final ServerStatusCacheManager cacheManager = ServerStatusCacheManager.getInstance();

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

            if (serverDTO.getOnlinePlayers() == null) {
                serverDTO.setOnlinePlayers(0);
            }
            if (serverDTO.getMaxPlayers() == null) {
                serverDTO.setMaxPlayers(0); // 적절한 기본값 (예: 20)을 설정할 수도 있습니다.
            }
            if (serverDTO.getStatus() == null) {
                serverDTO.setStatus("UNKNOWN"); // 등록 초기 상태
            }

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

        // API 호출을 캐시 매니저 호출로 대체로 인한 코드 변경

        ArrayList<ServerDTO> serverList = serverDAO.getServerList();

        return serverList;
    }

    // 2-2. 서버 단일 조회
    public ServerDTO getServerByIdService(Long serverId) {

        // API 호출을 캐시 매니저 호출로 대체로 인한 코드 변경

        ServerDTO serverDTO = serverDAO.getServerById(serverId);

        if (serverDTO == null) {
            throw new NoSuchElementException("ID " + serverId + "에 해당하는 서버를 찾을 수 없습니다.");
        }

        ServerStatusDTO statusDTO = cacheManager.getStatus(serverDTO.getDomain());
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

    // 6. 서버 카테고리
    public ArrayList<String> getAllCategoriesService() {
        return serverDAO.getAllCategories();
    }

    // 7. 서버 카테고리 필터링
    public ArrayList<ServerDTO> searchAndFilterServersService(String query, String category) {
        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        if (hasQuery && hasCategory) {
            // 🌟 케이스 1: 검색어 + 카테고리
            return serverDAO.searchAndFilter(query, category);
        } else if (hasCategory) {
            // 🌟 케이스 2: 카테고리만
            return serverDAO.filterServersByCategory(category);
        } else if (hasQuery) {
            // 🌟 케이스 3: 검색어만
            return serverDAO.searchServers(query);
        } else {
            // 🌟 케이스 4: 전체 목록
            return serverDAO.getServerList();
        }
    }

}
