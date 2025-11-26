package com.example.minecraft.dao;

import com.example.minecraft.util.JdbcConnectUtil;
import com.example.minecraft.dto.ServerDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServerDAO {

    // JDBC 작업
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // SQL문 상수화를 통한 유지보수 향상
    final String SQL_SERVER_CREATE = "INSERT INTO servers (name, status, version, domain) VALUES (?, ?, ?, ?);";

    final String SQL_SERVER_SELECT_LIST = "SELECT * FROM servers;";
    final String SQL_SERVER_SELECT_VIEW = "SELECT * FROM servers WHERE id = ?;";

    final String SQL_SERVER_DELETE = "DELETE FROM servers WHERE id = ?;";
    final String SQL_SERVER_UPDATE = "UPDATE servers set name = ?, status = ?, version = ?, domain = ? where id = ?;";

    // 1. 서버 생성
    public int createServer(ServerDTO serverDTO) {

        int result = 0;
        con = JdbcConnectUtil.getConnection();

        try {

            pstmt = con.prepareStatement(SQL_SERVER_CREATE);
            pstmt.setString(1, serverDTO.getName());
            pstmt.setString(2, serverDTO.getStatus());
            pstmt.setString(3, serverDTO.getVersion());
            pstmt.setString(4, serverDTO.getDomain());
            result = pstmt.executeUpdate();

            System.out.println("새로운 서버 " + serverDTO.getName() + "(이)가 생성되었습니다.");


        } catch (Exception e) {

            throw new RuntimeException(e);

        } finally {

            JdbcConnectUtil.close(con, pstmt);

        }

        return result;
    }

    // 2-1. 서버 목록 조회
    public ArrayList<ServerDTO> getServerList() {

        ArrayList<ServerDTO> list = new ArrayList<>();
        con = JdbcConnectUtil.getConnection();

        try {

            pstmt = con.prepareStatement(SQL_SERVER_SELECT_LIST);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ServerDTO dto = new ServerDTO();
                dto.setName(rs.getString("name"));
                dto.setStatus(rs.getString("status"));
                dto.setVersion(rs.getString("version"));
                dto.setDomain(rs.getString("domain"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                dto.setUpdatedAt(rs.getObject("created_at", LocalDateTime.class));
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }

        return list;
    }

    // 2-2. 서버 단일 조회
    public ServerDTO getServerById(Long serverId) {

        ServerDTO dto = null;
        con = JdbcConnectUtil.getConnection();

        try {

            pstmt = con.prepareStatement(SQL_SERVER_SELECT_VIEW);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new ServerDTO();
                dto.setName(rs.getString("name"));
                dto.setStatus(rs.getString("status"));
                dto.setVersion(rs.getString("version"));
                dto.setDomain(rs.getString("domain"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
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
            pstmt.setString(2, serverDTO.getStatus());
            pstmt.setString(3, serverDTO.getVersion());
            pstmt.setString(4, serverDTO.getDomain());
            pstmt.setObject(5, serverDTO.getServerId());
            result = pstmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
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
            JdbcConnectUtil.close(con, pstmt);
        }

        return result;

    }
}
