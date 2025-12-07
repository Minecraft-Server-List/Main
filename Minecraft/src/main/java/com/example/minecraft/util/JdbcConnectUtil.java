package com.example.minecraft.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcConnectUtil {

    // 1. Connection 획득
    public static Connection getConnection() {
        Connection con = null;
        try {
            // 커넥터 로딩
            Class.forName("com.mysql.cj.jdbc.Driver");
            // DB서버 커넥트
            // 🚨 주의: DB 접속 정보는 반드시 실제 환경에 맞게 수정해야 합니다.
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/minecraft", "root", "root");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("DB 연결 실패!");
            e.printStackTrace();
        }
        return con;
    }

    // 2. Connection만 닫는 안전한 메서드 (Service 레이어의 finally에서 사용)
    public static void close(Connection con) {
        if (con != null) {
            try {
                // 트랜잭션 후 autocommit 상태 복구
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 3. Statement/ResultSet만 닫는 안전한 메서드 (DAO에서 사용. try-with-resources 사용 시 생략 가능)
    public static void close(PreparedStatement pstmt, ResultSet rs) {

        // 🚨 Null 검사를 포함한 안전한 ResultSet 닫기
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 🚨 Null 검사를 포함한 안전한 PreparedStatement 닫기
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 🚨 이전 close(con, pstmt) 오버로드 메서드는 사용하지 않습니다.
    //    DAO에서 pstmt를 try-with-resources로 닫고, Service에서 close(con)만 사용하는 것이 가장 안전합니다.
}