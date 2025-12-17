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

    // [INSERT] 
    final String SQL_INSERT = "INSERT INTO base_board (user_id, board_category_id, title, content) VALUES (?, (SELECT board_category_id FROM board_category WHERE code = ?), ?, ?)";
    
    final String SQL_INCREASE_VIEW = "UPDATE base_board SET view_count = view_count + 1 WHERE base_board_id = ?";

    // [SELECT 기본 골격]
    final String BASE_SELECT = "SELECT b.*, u.name AS writer_name, bc.code AS cat_code, bc.name AS cat_name, "
            + "(SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count "
            + "FROM base_board b "
            + "JOIN users u ON b.user_id = u.user_id "
            + "JOIN board_category bc ON b.board_category_id = bc.board_category_id ";

    final String SQL_SELECT_PAGING = BASE_SELECT + "ORDER BY b.base_board_id DESC LIMIT ?, ?";
    
    final String SQL_SELECT_BY_CATEGORY = BASE_SELECT + "WHERE bc.code = ? ORDER BY b.base_board_id DESC LIMIT ?, ?";
    
    // [수정됨] 상세 조회용 SQL (is_liked 서브쿼리를 SELECT 절 안에 포함)
    final String SQL_SELECT_BY_ID = "SELECT b.*, u.name AS writer_name, bc.code AS cat_code, bc.name AS cat_name, "
            + "(SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id) AS like_count, "
            + "(SELECT COUNT(*) FROM board_likes bl WHERE bl.base_board_id = b.base_board_id AND bl.user_id = ?) AS is_liked "
            + "FROM base_board b "
            + "JOIN users u ON b.user_id = u.user_id "
            + "JOIN board_category bc ON b.board_category_id = bc.board_category_id "
            + "WHERE b.base_board_id = ?";
            
    final String SQL_SELECT_BY_USER_ID = BASE_SELECT + "WHERE b.user_id = ? ORDER BY b.base_board_id DESC";
    
    final String SQL_UPDATE = "UPDATE base_board SET title = ?, content = ? WHERE base_board_id = ?";
    final String SQL_DELETE = "DELETE FROM base_board WHERE base_board_id = ?";
    
    final String SQL_CHECK_LIKE = "SELECT 1 FROM board_likes WHERE user_id = ? AND base_board_id = ?";
    final String SQL_ADD_LIKE = "INSERT INTO board_likes (user_id, base_board_id) VALUES (?, ?)";
    final String SQL_REMOVE_LIKE = "DELETE FROM board_likes WHERE user_id = ? AND base_board_id = ?";

    public int insertBoard(BoardDTO dto) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_INSERT);
            pstmt.setLong(1, dto.getUserId());
            pstmt.setString(2, dto.getCategory()); 
            pstmt.setString(3, dto.getTitle());
            pstmt.setString(4, dto.getContent());
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { JdbcConnectUtil.close(pstmt, null); JdbcConnectUtil.close(con); }
    }

    public int incrementViewCount(long boardId) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_INCREASE_VIEW);
            pstmt.setLong(1, boardId);
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { JdbcConnectUtil.close(pstmt, null); JdbcConnectUtil.close(con); }
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
        finally { JdbcConnectUtil.close(pstmt, rs); JdbcConnectUtil.close(con); }
        return list;
    }

    public ArrayList<BoardDTO> selectBoardsByCategory(String categoryCode, int offset, int limit) {
        ArrayList<BoardDTO> list = new ArrayList<>();
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_CATEGORY);
            pstmt.setString(1, categoryCode);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToBoard(rs));
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(pstmt, rs); JdbcConnectUtil.close(con); }
        return list;
    }

    public BoardDTO selectBoardById(long boardId, long currentUserId) {
        BoardDTO dto = null;
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_SELECT_BY_ID);
            // 순서 주의: 첫번째 ?는 is_liked 확인용 user_id, 두번째 ?는 board_id
            pstmt.setLong(1, currentUserId);
            pstmt.setLong(2, boardId);
            rs = pstmt.executeQuery();
            if (rs.next()) dto = mapResultSetToBoard(rs);
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(pstmt, rs); JdbcConnectUtil.close(con); }
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
        finally { JdbcConnectUtil.close(pstmt, rs); JdbcConnectUtil.close(con); }
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
        finally { JdbcConnectUtil.close(pstmt, null); JdbcConnectUtil.close(con); }
    }

    public int deleteBoard(long boardId) {
        Connection con = null; PreparedStatement pstmt = null;
        try {
            con = JdbcConnectUtil.getConnection();
            pstmt = con.prepareStatement(SQL_DELETE);
            pstmt.setLong(1, boardId);
            return pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); return 0; } 
        finally { JdbcConnectUtil.close(pstmt, null); JdbcConnectUtil.close(con); }
    }

    public int toggleLike(long userId, long boardId) {
        int status = -1;
        Connection con = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            con = JdbcConnectUtil.getConnection();
            if(con == null) return -1;
            con.setAutoCommit(false); 

            pstmt = con.prepareStatement(SQL_CHECK_LIKE);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, boardId);
            rs = pstmt.executeQuery();
            boolean exists = rs.next();
            JdbcConnectUtil.close(pstmt, rs); 

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
            JdbcConnectUtil.close(pstmt, null); 
            JdbcConnectUtil.close(con); 
        }
        return status;
    }

 // [BoardDAO.java 맨 아래 부분 교체]
    private BoardDTO mapResultSetToBoard(ResultSet rs) throws SQLException {
        BoardDTO dto = new BoardDTO();
        dto.setBaseBoardId(rs.getLong("base_board_id"));
        dto.setUserId(rs.getLong("user_id"));
        
        // JOIN된 컬럼들 (SQL 별칭 주의)
        dto.setCategory(rs.getString("cat_code"));     
        dto.setCategoryName(rs.getString("cat_name")); 
        dto.setWriterName(rs.getString("writer_name"));
        
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setViewCount(rs.getInt("view_count"));
        dto.setLikeCount(rs.getInt("like_count"));

        // [수정 핵심] LocalDateTime 변환 안전하게 변경 (오류 발생 차단)
        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            dto.setCreatedAt(createdTs.toLocalDateTime());
        }

        java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            dto.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        // 좋아요 여부
        try {
            dto.setLiked(rs.getInt("is_liked") > 0); 
        } catch (SQLException e) {
            dto.setLiked(false);
        }
        
        return dto;
    }
}