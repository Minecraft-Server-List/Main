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
    // @return DB 정보 + API 상태 정보가 담긴 ServerDTO 목록
    public ArrayList<ServerDTO> getServerListService() {

        // 1. 서버 기본 목록 조회 (DAO에서 Connection 열고 닫음)
        ArrayList<ServerDTO> serverList = serverDAO.getServerList();

        Connection con = null;

        try {
            // 이미지 조회를 위한 Connection 획득 (반복문 밖에서 한 번만 열기)
            con = JdbcConnectUtil.getConnection();

            // 2. 각 서버별 이미지 및 상태 정보 조회 및 설정
            for (ServerDTO server : serverList) {

                // 2-1. 이미지 정보 조회 및 설정
                ServerImageDTO imageDTO = serverImageDAO.getServerImageByServerId(con, server.getServerId());
                server.setServerImage(imageDTO); // DTO에 이미지 정보 설정

                // 2-2. 마크 서버 상태 조회 및 설정 (기존 로직 유지)
                String apiUrl = API_BASE_URL + server.getDomain();
                String jsonResponse = apiClient.callApi(apiUrl);

                if (jsonResponse != null && !jsonResponse.isEmpty()) {
                    ServerStatusDTO statusDTO = gson.fromJson(jsonResponse, ServerStatusDTO.class);
                    server.setServerStatus(statusDTO);
                } else {
                    ServerStatusDTO statusDTO = new ServerStatusDTO();
                    statusDTO.setOnline(false);
                    server.setServerStatus(statusDTO);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("서버 상태 또는 이미지 조회 중 오류 발생: " + e.getMessage());
        } finally {
            // 3. 이미지 조회를 위해 열었던 Connection 닫기
            JdbcConnectUtil.close(con);
        }

        return serverList;
    }

    // 2-2. 서버 단일 조회
    public ServerDTO getServerByIdService(Long serverId) {

        // 1. 서버 기본 정보 조회 (DAO에서 Connection 열고 닫음)
        ServerDTO serverDTO = serverDAO.getServerById(serverId);

        if (serverDTO == null) {
            throw new NoSuchElementException("ID " + serverId + "에 해당하는 서버를 찾을 수 없습니다.");
        }

        Connection con = null;
        try {
            // 이미지 조회를 위한 Connection 획득
            con = JdbcConnectUtil.getConnection();

            // 2. 이미지 정보 조회 및 설정
            ServerImageDTO imageDTO = serverImageDAO.getServerImageByServerId(con, serverId);
            serverDTO.setServerImage(imageDTO);

            // 3. 마크 서버 상태 조회 및 설정 (기존 로직 유지)
            String apiUrl = API_BASE_URL + serverDTO.getDomain();
            String jsonResponse = apiClient.callApi(apiUrl);

            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                ServerStatusDTO statusDTO = gson.fromJson(jsonResponse, ServerStatusDTO.class);
                serverDTO.setServerStatus(statusDTO);
            } else {
                ServerStatusDTO statusDTO = new ServerStatusDTO();
                statusDTO.setOnline(false);
                serverDTO.setServerStatus(statusDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("서버 상태 또는 이미지 조회 중 오류 발생: " + e.getMessage());
        } finally {
            // 4. 이미지 조회를 위해 열었던 Connection 닫기
            JdbcConnectUtil.close(con);
        }

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

}
