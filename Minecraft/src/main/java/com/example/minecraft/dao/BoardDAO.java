package com.example.minecraft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.minecraft.util.JdbcConnectUtil;
import com.example.minecraft.dto.BoardDTO;

public class BoardDAO {
    // 🚨 전역 변수 삭제 완료 (Connection con = null; 등 없음)

    // --- SQL 쿼리 ---
    final String SQL_INSERT = "INSERT INTO base_board (user_id, category, title, content) VALUES (?, ?, ?, ?)";
    final String SQL_INCREASE_VIEW = "UPDATE base_board SET view_count = view_count + 1 WHERE base_board_id = ?";
    
    final String SQL_SELECT_ALL = 
            "SELECT b.*, u.name AS writer_name, (SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count, 0 AS is_liked FROM base_board b JOIN users u ON b.user_id = u.user_id ORDER BY b.base_board_id DESC";
    
    final String SQL_SELECT_PAGING = 
            "SELECT b.*, u.name AS writer_name, (SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count, 0 AS is_liked FROM base_board b JOIN users u ON b.user_id = u.user_id ORDER BY b.base_board_id DESC LIMIT ?, ?";
    
    final String SQL_SELECT_BY_CATEGORY = 
            "SELECT b.*, u.name AS writer_name, (SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count, 0 AS is_liked FROM base_board b JOIN users u ON b.user_id = u.user_id WHERE b.category = ? ORDER BY b.base_board_id DESC LIMIT ?, ?";
    
    final String SQL_SELECT_BY_ID = 
            "SELECT b.*, u.name AS writer_name, (SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count, (SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id AND bl.user_id = ?) AS is_liked FROM base_board b JOIN users u ON b.user_id = u.user_id WHERE b.base_board_id = ?";
    
    final String SQL_SELECT_BY_USER_ID = 
            "SELECT b.*, u.name AS writer_name, (SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count, 0 AS is_liked FROM base_board b JOIN users u ON b.user_id = u.user_id WHERE b.user_id = ? ORDER BY b.base_board_id DESC";
    
    final String SQL_UPDATE = "UPDATE base_board SET title = ?, content = ? WHERE base_board_id = ?";
    final String SQL_DELETE = "DELETE FROM base_board WHERE base_board_id = ?";
    
    final String SQL_CHECK_LIKE = "SELECT 1 FROM board_likes WHERE user_id = ? AND base_board_id = ?";
    final String SQL_ADD_LIKE = "INSERT INTO board_likes (user_id, base_board_id) VALUES (?, ?)";
    final String SQL_REMOVE_LIKE = "DELETE FROM board_likes WHERE user_id = ? AND base_board_id = ?";

    // --- 메서드 구현 (지역 변수 사용) ---

    public int insertBoard(BoardDTO dto) {
        Connection con = null; PreparedStatement pstmt = null;
        int result = 0;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_INSERT);
            pstmt.setLong(1, dto.getUserId());
            pstmt.setString(2, dto.getCategory());
            pstmt.setString(3, dto.getTitle());
            pstmt.setString(4, dto.getContent());
            result = pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt); }
        return result;
    }

    public int incrementViewCount(long boardId) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_INCREASE_VIEW);
            pstmt.setLong(1, boardId);
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { JdbcConnectUtil.close(con, pstmt); }
    }

    public ArrayList<BoardDTO> selectAllBoards(int offset, int limit) {
        ArrayList<BoardDTO> list = new ArrayList<>();
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_PAGING);
            pstmt.setInt(1, offset);
            pstmt.setInt(2, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToBoard(rs));
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return list;
    }

    public ArrayList<BoardDTO> selectBoardsByCategory(String category, int offset, int limit) {
        ArrayList<BoardDTO> list = new ArrayList<>();
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_CATEGORY);
            pstmt.setString(1, category);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToBoard(rs));
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return list;
    }

    public BoardDTO selectBoardById(long boardId, long currentUserId) {
        BoardDTO dto = null;
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_ID);
            pstmt.setLong(1, currentUserId);
            pstmt.setLong(2, boardId);
            rs = pstmt.executeQuery();
            if (rs.next()) dto = mapResultSetToBoard(rs);
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return dto;
    }

    public ArrayList<BoardDTO> selectBoardsByUserId(long userId) {
        ArrayList<BoardDTO> list = new ArrayList<>();
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_USER_ID);
            pstmt.setLong(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToBoard(rs));
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return list;
    }

    public int updateBoard(BoardDTO dto) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_UPDATE);
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setLong(3, dto.getBaseBoardId());
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { JdbcConnectUtil.close(con, pstmt); }
    }

    public int deleteBoard(long boardId) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_DELETE);
            pstmt.setLong(1, boardId);
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { JdbcConnectUtil.close(con, pstmt); }
    }

    // [핵심 수정] 트랜잭션 처리 (지역변수 사용 + Null 체크)
    public int toggleLike(long userId, long boardId) {
        int status = -1;
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        
        try {
            con = JdbcConnectUtil.getConnection();
            if(con == null) return -1; // 연결 실패 시 종료

            con.setAutoCommit(false); 

            pstmt = con.prepareStatement(SQL_CHECK_LIKE);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, boardId);
            rs = pstmt.executeQuery();
            boolean exists = rs.next();
            JdbcConnectUtil.close(null, pstmt, rs); 

            if (exists) {
                pstmt = con.prepareStatement(SQL_REMOVE_LIKE);
                pstmt.setLong(1, userId);
                pstmt.setLong(2, boardId);
                pstmt.executeUpdate();
                status = 0;
            } else {
                pstmt = con.prepareStatement(SQL_ADD_LIKE);
                pstmt.setLong(1, userId);
                pstmt.setLong(2, boardId);
                pstmt.executeUpdate();
                status = 1;
            }
            con.commit(); 
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException e) {}
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return status;
    }

    private BoardDTO mapResultSetToBoard(ResultSet rs) throws SQLException {
        BoardDTO dto = new BoardDTO();
        dto.setBaseBoardId(rs.getLong("base_board_id"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setCategory(rs.getString("category"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        dto.setWriterName(rs.getString("writer_name"));
        dto.setViewCount(rs.getInt("view_count"));
        dto.setLikeCount(rs.getInt("like_count"));
        dto.setLiked(rs.getInt("is_liked") > 0); 
        return dto;
    }
}