package com.example.minecraft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.minecraft.util.JdbcConnectUtil;
import com.example.minecraft.dto.ComentDTO;

public class ComentDAO {

    final String SQL_INSERT = "INSERT INTO coment (user_id, base_board_id, content) VALUES (?, ?, ?)";
    final String SQL_SELECT_BY_BOARD = "SELECT c.*, u.name AS writer_name, (SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id) AS like_count, (SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id AND cl.user_id = ?) AS is_liked FROM coment c JOIN users u ON c.user_id = u.user_id WHERE c.base_board_id = ? ORDER BY c.coment_id ASC";
    final String SQL_SELECT_BY_USER = "SELECT c.*, b.title AS board_title, (SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id) AS like_count, (SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id AND cl.user_id = ?) AS is_liked FROM coment c JOIN base_board b ON c.base_board_id = b.base_board_id WHERE c.user_id = ? ORDER BY c.coment_id DESC";
    final String SQL_UPDATE = "UPDATE coment SET content = ? WHERE coment_id = ?";
    final String SQL_DELETE = "DELETE FROM coment WHERE coment_id = ?";
    
    final String SQL_CHECK_LIKE = "SELECT 1 FROM comment_likes WHERE user_id = ? AND coment_id = ?";
    final String SQL_ADD_LIKE = "INSERT INTO comment_likes (user_id, coment_id) VALUES (?, ?)";
    final String SQL_REMOVE_LIKE = "DELETE FROM comment_likes WHERE user_id = ? AND coment_id = ?";

    public int insertComent(ComentDTO dto) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_INSERT);
            pstmt.setLong(1, dto.getUserId());
            pstmt.setLong(2, dto.getBaseBoardId());
            pstmt.setString(3, dto.getContent());
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { 
            JdbcConnectUtil.close(pstmt, null); 
            JdbcConnectUtil.close(con); 
        }
    }

    public int updateComent(long comentId, String content) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_UPDATE);
            pstmt.setString(1, content);
            pstmt.setLong(2, comentId);
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { 
            JdbcConnectUtil.close(pstmt, null); 
            JdbcConnectUtil.close(con); 
        }
    }

    public ArrayList<ComentDTO> selectComentsByBoardId(long boardId, long currentUserId) {
        ArrayList<ComentDTO> list = new ArrayList<>();
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_BOARD);
            pstmt.setLong(1, currentUserId);
            pstmt.setLong(2, boardId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ComentDTO dto = mapResultSetToComent(rs);
                dto.setWriterName(rs.getString("writer_name"));
                list.add(dto);
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { 
            JdbcConnectUtil.close(pstmt, rs); 
            JdbcConnectUtil.close(con); 
        }
        return list;
    }

    public ArrayList<ComentDTO> selectComentsByUserId(long userId) {
        ArrayList<ComentDTO> list = new ArrayList<>();
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_USER);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ComentDTO dto = mapResultSetToComent(rs);
                dto.setBoardTitle(rs.getString("board_title"));
                list.add(dto);
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { 
            JdbcConnectUtil.close(pstmt, rs); 
            JdbcConnectUtil.close(con); 
        }
        return list;
    }

    public int deleteComent(long comentId) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_DELETE);
            pstmt.setLong(1, comentId);
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { 
            JdbcConnectUtil.close(pstmt, null); 
            JdbcConnectUtil.close(con); 
        }
    }

    public int toggleLike(long userId, long comentId) {
        int status = -1;
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            if(con == null) return -1;

            con.setAutoCommit(false); 
            pstmt = con.prepareStatement(SQL_CHECK_LIKE);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, comentId);
            rs = pstmt.executeQuery();
            boolean exists = rs.next();
            
            JdbcConnectUtil.close(pstmt, rs); 

            if (exists) {
                pstmt = con.prepareStatement(SQL_REMOVE_LIKE);
                pstmt.setLong(1, userId);
                pstmt.setLong(2, comentId);
                pstmt.executeUpdate();
                status = 0;
            } else {
                pstmt = con.prepareStatement(SQL_ADD_LIKE);
                pstmt.setLong(1, userId);
                pstmt.setLong(2, comentId);
                pstmt.executeUpdate();
                status = 1;
            }
            con.commit(); 
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        } finally {
            JdbcConnectUtil.close(pstmt, null); 
            JdbcConnectUtil.close(con); 
        }
        return status;
    }

    private ComentDTO mapResultSetToComent(ResultSet rs) throws SQLException {
        ComentDTO dto = new ComentDTO();
        dto.setComentId(rs.getLong("coment_id"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setBaseBoardId(rs.getLong("base_board_id"));
        dto.setContent(rs.getString("content"));
        dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        dto.setLikeCount(rs.getInt("like_count"));
        dto.setLiked(rs.getInt("is_liked") > 0);
        return dto;
    }
}