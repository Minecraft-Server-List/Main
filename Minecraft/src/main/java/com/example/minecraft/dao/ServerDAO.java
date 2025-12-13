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

    final String SQL_SELECT_ALL_CATEGORIES = "SELECT DISTINCT name FROM category";

    private static final String SQL_SEARCH_AND_FILTER =
            "SELECT s.server_id, s.name, s.description, s.status, s.online_players, s.max_players, s.version, s.domain, s.created_at, s.updated_at, " +
                    "MAX(c.name) AS category_name, " +
                    "si.file_name AS image_file_name " +
                    "FROM servers s " +
                    "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
                    "LEFT JOIN category c ON sc.category_id = c.category_id " +
                    "LEFT JOIN server_image si ON s.server_id = si.server_id " +
                    "WHERE c.name = ? " + // 🌟 1. 카테고리 필터
                    "AND (s.name LIKE ? OR s.description LIKE ?) " + // 🌟 2. 검색어 필터
                    "GROUP BY s.server_id " +
                    "ORDER BY s.created_at DESC";

    private static final String SQL_FILTER_SERVERS_BY_CATEGORY =
            "SELECT s.server_id, s.name, s.description, s.status, s.online_players, s.max_players, s.version, s.domain, s.created_at, s.updated_at, " +
                    "MAX(c.name) AS category_name, " +
                    "si.file_name AS image_file_name " +
                    "FROM servers s " +
                    "LEFT JOIN server_category sc ON s.server_id = sc.server_id " +
                    "LEFT JOIN category c ON sc.category_id = c.category_id " +
                    "LEFT JOIN server_image si ON s.server_id = si.server_id " +
                    "WHERE c.name = ? " +
                    "GROUP BY s.server_id " +
                    "ORDER BY s.created_at DESC";

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

    // 8. 서버 카테고리
    public ArrayList<String> getAllCategories() {
        ArrayList<String> categoryList = new ArrayList<>();
        Connection con = null;

        try {
            con = JdbcConnectUtil.getConnection();

            // 🚨 DB 연결 및 쿼리 실행
            try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL_CATEGORIES);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    // "name" 컬럼의 문자열을 리스트에 추가합니다.
                    categoryList.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("카테고리 목록 조회 중 SQL 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return categoryList;
    }

    // 9. 서버 카테고리 필터링
    public ArrayList<ServerDTO> filterServersByCategory(String categoryName) {
        ArrayList<ServerDTO> serverList = new ArrayList<>();
        Connection con = null;

        try {
            con = JdbcConnectUtil.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(SQL_FILTER_SERVERS_BY_CATEGORY)) {

                pstmt.setString(1, categoryName); // 🌟 카테고리 이름 바인딩

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {

                        // 🌟 1. ServerDTO 기본 정보 설정
                        ServerDTO server = new ServerDTO();
                        server.setServerId(rs.getLong("server_id"));
                        server.setName(rs.getString("name"));
                        server.setDescription(rs.getString("description"));
                        server.setStatus(rs.getString("status"));
                        server.setOnlinePlayers(rs.getInt("online_players"));
                        server.setMaxPlayers(rs.getInt("max_players"));
                        server.setVersion(rs.getString("version"));
                        server.setDomain(rs.getString("domain"));

                        // 🌟 2. 카테고리 이름 설정 (MAX(c.name) AS category_name)
                        server.setCategory(rs.getString("category_name"));
                        server.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        server.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                        // 🌟 3. 이미지 정보 설정 (ServerImageDTO가 필요할 경우)
                        String imageFileName = rs.getString("image_file_name");
                        if (imageFileName != null) {
                            ServerImageDTO imageDTO = new ServerImageDTO();
                            imageDTO.setFileName(imageFileName);
                            // 파일 경로, ID 등 다른 필드도 필요하면 여기서 설정해야 합니다.
                            server.setServerImage(imageDTO);
                        }

                        serverList.add(server);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("카테고리 필터링 중 SQL 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return serverList;
    }

    // 10. 통합 검색
    public ArrayList<ServerDTO> searchAndFilter(String query, String categoryName) {
        ArrayList<ServerDTO> serverList = new ArrayList<>();
        Connection con = null;

        try {
            con = JdbcConnectUtil.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(SQL_SEARCH_AND_FILTER)) {

                // 🌟 1. 바인딩: 카테고리
                pstmt.setString(1, categoryName);

                // 🌟 2 & 3. 바인딩: 검색어 (LIKE 검색을 위해 %를 추가)
                String searchPattern = "%" + query + "%";
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        // 🌟 ServerDTO 객체 생성 및 ResultSet 매핑 로직 (filterServersByCategory와 동일)
                        ServerDTO server = new ServerDTO();
                        server.setServerId(rs.getLong("server_id"));
                        server.setName(rs.getString("name"));
                        server.setDescription(rs.getString("description"));
                        server.setStatus(rs.getString("status"));
                        server.setOnlinePlayers(rs.getInt("online_players"));
                        server.setMaxPlayers(rs.getInt("max_players"));
                        server.setVersion(rs.getString("version"));
                        server.setDomain(rs.getString("domain"));
                        server.setCategory(rs.getString("category_name"));

                        String imageFileName = rs.getString("image_file_name");
                        if (imageFileName != null) {
                            ServerImageDTO imageDTO = new ServerImageDTO();
                            imageDTO.setFileName(imageFileName);
                            // 파일 경로, ID 등 다른 필드도 필요하면 여기서 설정해야 합니다.
                            server.setServerImage(imageDTO);
                        }

                        serverList.add(server);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("통합 검색/필터링 중 SQL 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con);
        }
        return serverList;
    }
}