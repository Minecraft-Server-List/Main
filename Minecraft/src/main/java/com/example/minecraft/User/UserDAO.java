package com.example.minecraft.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.minecraft.Util.JdbcConnectUtil;

public class UserDAO {
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // --- SQL 쿼리 상수 ---
    final String SQL_ALL = "select * from users;";
    final String SQL_LOGIN_CHECK = "select * from users where email=? and password=?;";
    final String SQL_INSERT = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
    final String SQL_SELECT_BY_EMAIL = "SELECT * FROM users WHERE email = ?;";
    final String SQL_UPDATE = "UPDATE users SET name = ?, email = ?, password = ?, role = ? WHERE user_id = ?;";
    final String SQL_DELETE = "DELETE FROM users WHERE user_id = ?;";
    
    // JSP의 수정/삭제 기능에 필수
    final String SQL_SELECT_BY_ID = "SELECT * FROM users WHERE user_id = ?;";


    /**
     * [SELECT] 모든 사용자 조회
     */
    public ArrayList<UserDTO> SelectAll() {
        ArrayList<UserDTO> uList = new ArrayList<>();
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_ALL);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                UserDTO dto = new UserDTO();
                dto.setUserId(rs.getLong("user_id"));
                dto.setPassword(rs.getString("password"));
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setRole(rs.getString("role"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                uList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return uList;
    }

    /**
     * [SELECT] 로그인 체크
     */
    public boolean loginCheck(UserDTO udto) {
        boolean loginCheck = false;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_LOGIN_CHECK);
            pstmt.setString(1, udto.getEmail());
            pstmt.setString(2, udto.getPassword());
            rs = pstmt.executeQuery();
            loginCheck = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return loginCheck;
    }

    /**
     * [INSERT] 신규 사용자 추가
     */
    public int insertUser(UserDTO udto) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_INSERT);
            pstmt.setString(1, udto.getName());
            pstmt.setString(2, udto.getEmail());
            pstmt.setString(3, udto.getPassword()); 
            pstmt.setString(4, udto.getRole());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
        }
        return result;
    }

    /**
     * [SELECT BY EMAIL] 사용자 1명 조회 (이메일 검색)
     */
    public UserDTO selectUserByEmail(String email) {
        UserDTO dto = null;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_SELECT_BY_EMAIL);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dto = new UserDTO();
                dto.setUserId(rs.getLong("user_id"));
                dto.setPassword(rs.getString("password"));
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setRole(rs.getString("role"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return dto;
    }

    /**
     * [SELECT BY ID] 사용자 1명 조회 (ID 검색)
     */
    public UserDTO selectUserById(long userId) {
        UserDTO dto = null;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_SELECT_BY_ID);
            pstmt.setLong(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dto = new UserDTO();
                dto.setUserId(rs.getLong("user_id"));
                dto.setPassword(rs.getString("password"));
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setRole(rs.getString("role"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return dto;
    }


    /**
     * [UPDATE] 사용자 정보 수정 (user_id 기반)
     */
    public int updateUser(UserDTO udto) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_UPDATE);
            pstmt.setString(1, udto.getName());
            pstmt.setString(2, udto.getEmail());
            pstmt.setString(3, udto.getPassword());
            pstmt.setString(4, udto.getRole());
            pstmt.setLong(5, udto.getUserId());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
        }
        return result;
    }

    /**
     * [DELETE] 사용자 삭제 (user_id 기반)
     */
    public int deleteUser(long userId) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_DELETE);
            pstmt.setLong(1, userId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
        }
        return result;
    }
}