package com.example.minecraft.dao;

import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.dto.ServerImageDTO;
import com.example.minecraft.util.JdbcConnectUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServerDAO {

    // JDBC 작업
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // SQL문 상수화를 통한 유지보수 향상
    final String SQL_SERVER_CREATE = "INSERT INTO servers (name, description, status, version, domain) VALUES (?, ?, ?, ?, ?);";

    final String SQL_SERVER_SEARCH = "SELECT " +
                                        "    s.server_id, s.name, s.description, s.status, s.version, s.domain, s.created_at, s.updated_at, " +
                                        "    MAX(c.name) AS category_name, " +
                                        "    si.file_name AS image_file_name " +
                                        "FROM servers s " +
                                        "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
                                        "LEFT JOIN category c ON sc.category_id = c.category_id " +
                                        "LEFT JOIN server_image si ON s.server_id = si.server_id " +
                                        "WHERE s.name LIKE ?" +
                                        "GROUP BY s.server_id " +
                                        "ORDER BY s.created_at DESC";

    final String SQL_SERVER_SELECT_LIST = "SELECT " +
                                            "    s.server_id, s.name, s.description, s.status, s.version, s.domain, s.created_at, s.updated_at, " +
                                            "    MAX(c.name) AS category_name, " +
                                            "    si.file_name AS image_file_name " +
                                            "FROM servers s " +
                                            "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
                                            "LEFT JOIN category c ON sc.category_id = c.category_id " +
                                            "LEFT JOIN server_image si ON s.server_id = si.server_id " +
                                            "GROUP BY s.server_id " +
                                            "ORDER BY s.created_at DESC";

    final String SQL_SERVER_SELECT_VIEW = "SELECT " +
                                            "    s.server_id, s.name, s.description, s.status, s.version, s.domain, s.created_at, s.updated_at, " +
                                            "    MAX(c.name) AS category_name, " +
                                            "    si.file_name AS image_file_name " +
                                            "FROM servers s " +
                                            "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
                                            "LEFT JOIN category c ON sc.category_id = c.category_id " +
                                            "LEFT JOIN server_image si ON s.server_id = si.server_id " +
                                            "WHERE s.server_id = ? " +
                                            "GROUP BY s.server_id";

    final String SQL_SERVER_DELETE = "DELETE FROM servers WHERE server_id = ?;";
    final String SQL_SERVER_UPDATE = "UPDATE servers set name = ?, description = ?, status = ?, version = ?, domain = ? where server_id = ?;";

    // 1. 서버 생성
    public long createServer(Connection con, ServerDTO serverDTO) throws SQLException {

        long generatedId = -1;
        try (PreparedStatement pstmt = con.prepareStatement(SQL_SERVER_CREATE, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, serverDTO.getName());
            pstmt.setString(2, serverDTO.getDescription());
            pstmt.setString(3, serverDTO.getStatus());
            pstmt.setString(4, serverDTO.getVersion());
            pstmt.setString(5, serverDTO.getDomain());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getLong(1);
                }
            }
        }
        if (generatedId == -1) {
            throw new SQLException("서버 등록에 실패했습니다. 생성된 ID를 얻지 못했습니다.");
        }
        return generatedId;
    }

    // 2-1. 서버 목록 조회
    public ArrayList<ServerDTO> getServerList() {
        ArrayList<ServerDTO> list = new ArrayList<>();
        Connection con = null;

        try {
            con = JdbcConnectUtil.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(SQL_SERVER_SELECT_LIST);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    ServerDTO dto = new ServerDTO();
                    dto.setServerId(rs.getLong("server_id"));
                    dto.setName(rs.getString("name"));
                    dto.setDescription(rs.getString("description"));
                    dto.setStatus(rs.getString("status"));
                    dto.setVersion(rs.getString("version"));
                    dto.setDomain(rs.getString("domain"));
                    dto.setCategory(rs.getString("category_name"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    String imageFileName = rs.getString("image_file_name");
                    if (imageFileName != null) {
                        ServerImageDTO imageDTO = new ServerImageDTO();
                        imageDTO.setFileName(imageFileName);
                        dto.setServerImage(imageDTO);
                    }
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return list;
    }

    // 2-2. 서버 단일 조회
    public ServerDTO getServerById(Long serverId) {
        ServerDTO dto = null;
        Connection con = null;

        try {
            con = JdbcConnectUtil.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(SQL_SERVER_SELECT_VIEW)) {
                pstmt.setLong(1, serverId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        dto = new ServerDTO();
                        dto.setServerId(rs.getLong("server_id"));
                        dto.setName(rs.getString("name"));
                        dto.setDescription(rs.getString("description"));
                        dto.setStatus(rs.getString("status"));
                        dto.setVersion(rs.getString("version"));
                        dto.setDomain(rs.getString("domain"));
                        dto.setCategory(rs.getString("category_name"));
                        dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                        String imageFileName = rs.getString("image_file_name");
                        if (imageFileName != null) {
                            // ServerImageDTO를 생성하여 파일명 설정
                            ServerImageDTO imageDTO = new ServerImageDTO();
                            imageDTO.setFileName(imageFileName);
                            dto.setServerImage(imageDTO);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return dto;
    }

    // 3. 서버 수정
    public int updateServer(ServerDTO serverDTO) {

        int result = 0;
        con = JdbcConnectUtil.getConnection();

        try {

            pstmt = con.prepareStatement(SQL_SERVER_UPDATE);
            pstmt.setString(1, serverDTO.getName());
            pstmt.setString(2, serverDTO.getDescription());
            pstmt.setString(3, serverDTO.getStatus());
            pstmt.setString(4, serverDTO.getVersion());
            pstmt.setString(5, serverDTO.getDomain());
            pstmt.setObject(6, serverDTO.getServerId());
            result = pstmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }

        return result;

    }

    // 4. 서버 삭제
    public int deleteServerById(Long serverId) {

        int result = 0;

        con = JdbcConnectUtil.getConnection();

        try {

            pstmt = con.prepareStatement(SQL_SERVER_DELETE);
            pstmt.setLong(1, serverId);
            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }

        return result;

    }

    // 5. 서버 검색 기능
    public ArrayList<ServerDTO> searchServers(String query) {
        ArrayList<ServerDTO> list = new ArrayList<>();
        Connection con = null;

        try {
            con = JdbcConnectUtil.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(SQL_SERVER_SEARCH)) {

                // 💡 검색어를 %query% 형태로 바인딩하여 LIKE 연산자에 사용
                String searchPattern = "%" + query + "%";

                pstmt.setString(1, searchPattern); // name LIKE ?
                // pstmt.setString(2, searchPattern); // description LIKE ?

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ServerDTO dto = new ServerDTO();
                        dto.setServerId(rs.getLong("server_id"));
                        dto.setName(rs.getString("name"));
                        dto.setDescription(rs.getString("description"));
                        dto.setStatus(rs.getString("status"));
                        dto.setVersion(rs.getString("version"));
                        dto.setDomain(rs.getString("domain"));
                        dto.setCategory(rs.getString("category_name"));
                        dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                        String imageFileName = rs.getString("image_file_name");
                        if (imageFileName != null) {
                            // ServerImageDTO를 생성하여 파일명 설정
                            ServerImageDTO imageDTO = new ServerImageDTO();
                            imageDTO.setFileName(imageFileName);
                            dto.setServerImage(imageDTO);
                        }
                        list.add(dto);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return list;
    }
}
