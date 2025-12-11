package com.example.minecraft.dao;

import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.dto.ServerImageDTO;
import com.example.minecraft.util.JdbcConnectUtil;

import java.sql.*;
import java.util.ArrayList;

public class ServerDAO {

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    final String SQL_SERVER_CREATE = "INSERT INTO servers (name, description, status, online_players, max_players, version, domain) VALUES (?, ?, ?, ?, ?, ?, ?);";

    final String SQL_SERVER_SEARCH = "SELECT " +
            "    s.server_id, s.name, s.description, s.status, s.online_players, s.max_players, s.version, s.domain, s.created_at, s.updated_at, " +
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
            "    s.server_id, s.name, s.description, s.status, s.online_players, s.max_players, s.version, s.domain, s.created_at, s.updated_at, " +
            "    MAX(c.name) AS category_name, " +
            "    si.file_name AS image_file_name " +
            "FROM servers s " +
            "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
            "LEFT JOIN category c ON sc.category_id = c.category_id " +
            "LEFT JOIN server_image si ON s.server_id = si.server_id " +
            "GROUP BY s.server_id " +
            "ORDER BY s.created_at DESC";

    final String SQL_SERVER_SELECT_VIEW = "SELECT " +
            "    s.server_id, s.name, s.description, s.status, s.online_players, s.max_players, s.version, s.domain, s.created_at, s.updated_at, " +
            "    MAX(c.name) AS category_name, " +
            "    si.file_name AS image_file_name " +
            "FROM servers s " +
            "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
            "LEFT JOIN category c ON sc.category_id = c.category_id " +
            "LEFT JOIN server_image si ON s.server_id = si.server_id " +
            "WHERE s.server_id = ? " +
            "GROUP BY s.server_id";

    final String SQL_SERVER_DELETE = "DELETE FROM servers WHERE server_id = ?;";
    final String SQL_SERVER_UPDATE = "UPDATE servers set name = ?, description = ?, status = ?, online_players = ?, max_players = ?, version = ?, domain = ? where server_id = ?;";

    // 1. 서버 생성
    public long createServer(Connection con, ServerDTO serverDTO) throws SQLException {

        long generatedId = -1;
        try (PreparedStatement pstmt = con.prepareStatement(SQL_SERVER_CREATE, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, serverDTO.getName());
            pstmt.setString(2, serverDTO.getDescription());
            pstmt.setString(3, serverDTO.getStatus());
            pstmt.setInt(4, serverDTO.getOnlinePlayers());
            pstmt.setInt(5, serverDTO.getMaxPlayers());
            pstmt.setString(6, serverDTO.getVersion());
            pstmt.setString(7, serverDTO.getDomain());
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
                    dto.setOnlinePlayers(rs.getInt("online_players"));
                    dto.setMaxPlayers(rs.getInt("max_players"));
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
                        dto.setOnlinePlayers(rs.getInt("online_players"));
                        dto.setMaxPlayers(rs.getInt("max_players"));
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
            pstmt.setInt(4, serverDTO.getOnlinePlayers());
            pstmt.setInt(5, serverDTO.getMaxPlayers());
            pstmt.setString(6, serverDTO.getVersion());
            pstmt.setString(7, serverDTO.getDomain());
            pstmt.setObject(8, serverDTO.getServerId());
            result = pstmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(pstmt, null);
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
            JdbcConnectUtil.close(pstmt, null);
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

                String searchPattern = "%" + query + "%";

                pstmt.setString(1, searchPattern);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ServerDTO dto = new ServerDTO();
                        dto.setServerId(rs.getLong("server_id"));
                        dto.setName(rs.getString("name"));
                        dto.setDescription(rs.getString("description"));
                        dto.setStatus(rs.getString("status"));
                        dto.setOnlinePlayers(rs.getInt("online_players"));
                        dto.setMaxPlayers(rs.getInt("max_players"));
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return list;
    }

    // 6. 아이디 및 도메인 조회
    public ArrayList<ServerDTO> getAllServerDomains() {
        ArrayList<ServerDTO> serverList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT server_id, domain FROM servers WHERE status != 'DELETED'";

        try {
            conn = JdbcConnectUtil.getConnection();

            if (conn == null) {
                System.err.println("DB 연결 실패로 인해 서버 도메인 조회를 건너뜁니다.");
                return serverList;
            }

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ServerDTO server = new ServerDTO();
                server.setServerId(rs.getLong("server_id"));
                server.setDomain(rs.getString("domain"));
                serverList.add(server);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: [ServerDAO] getAllServerDomains 조회 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(pstmt, rs);
            JdbcConnectUtil.close(conn);
        }

        return serverList;
    }

    // 7. 서버 정보 업데이트
    public int updateServerStatus(long serverId, int onlinePlayers, int maxPlayers, String status) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE servers SET online_players = ?, max_players = ?, status = ?, updated_at = NOW() " +
                "WHERE server_id = ?";

        try {
            conn = JdbcConnectUtil.getConnection();

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, onlinePlayers);
            pstmt.setInt(2, maxPlayers);
            pstmt.setString(3, status);
            pstmt.setLong(4, serverId);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR: [ServerDAO] updateServerStatus 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(pstmt, null);
            JdbcConnectUtil.close(conn);
        }

        return result;
    }
}