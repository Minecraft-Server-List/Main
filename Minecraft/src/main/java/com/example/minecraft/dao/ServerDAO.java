package com.example.minecraft.dao;

import com.example.minecraft.Util.JdbcConnectUtil;
import com.example.minecraft.dto.ServerDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServerDAO {

    // JDBC 작업
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // SQL문 상수화를 통한 유지보수 향상
    final String SQL_SERVER_CREATE = "INSERT INTO servers (name, status, version, domain) VALUES (?, ?, ?, ?)";

    // 1. 서버 생성
    public int createServer(ServerDTO serverDTO) {
        con = JdbcConnectUtil.getConnection();

        try {

            pstmt = con.prepareStatement(SQL_SERVER_CREATE);
            pstmt.setString(1, serverDTO.getName());
            pstmt.setString(2, serverDTO.getStatus());
            pstmt.setString(3, serverDTO.getVersion());
            pstmt.setString(4, serverDTO.getDomain());
            pstmt.executeUpdate();

            System.out.println("새로운 서버 " + serverDTO.getName() + "(이)가 생성되었습니다.");


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }

        return 1;
    }
}
