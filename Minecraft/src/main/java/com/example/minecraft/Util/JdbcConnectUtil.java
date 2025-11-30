package com.example.minecraft.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcConnectUtil {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // [중요] serverTimezone 및 인코딩 설정 추가 (URL이 맞는지 확인 필수)
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/jjap?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&autoReconnect=true", 
                "root", 
                "3274"
            );
        } catch (ClassNotFoundException e) {
            System.err.println("!!! [DB 에러] 드라이버 로딩 실패 (mysql-connector-j.jar 확인 필요) !!!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("!!! [DB 에러] 연결 실패 (URL, ID, PW 확인 필요) !!!");
            System.err.println("에러 메시지: " + e.getMessage());
        }
        return con;
    }
    
    public static void close(Connection con, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close(); // [핵심] null 체크 추가
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (con != null) con.close(); // [핵심] null 체크 추가
        } catch (SQLException e) { e.printStackTrace(); }
    }
}