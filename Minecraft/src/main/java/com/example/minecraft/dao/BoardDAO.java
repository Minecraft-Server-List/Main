package com.example.minecraft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.minecraft.Util.JdbcConnectUtil;
import com.example.minecraft.dto.BoardDTO;

public class BoardDAO {
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    final String SQL_INSERT = "INSERT INTO base_board (user_id, title, content) VALUES (?, ?, ?)";
    
    // [수정] 전체 목록 조회 (페이징 없음)
    final String SQL_SELECT_ALL = 
            "SELECT b.*, u.name AS writer_name FROM base_board b " +
            "JOIN users u ON b.user_id = u.user_id ORDER BY b.base_board_id DESC";

    // [신규] 페이징 적용 목록 조회 (LIMIT offset, count)
    final String SQL_SELECT_PAGING = 
            "SELECT b.*, u.name AS writer_name FROM base_board b " +
            "JOIN users u ON b.user_id = u.user_id " +
            "ORDER BY b.base_board_id DESC LIMIT ?, ?";

    final String SQL_SELECT_BY_ID = 
            "SELECT b.*, u.name AS writer_name FROM base_board b " +
            "JOIN users u ON b.user_id = u.user_id WHERE b.base_board_id = ?";
    
    final String SQL_SELECT_BY_USER_ID = 
            "SELECT b.*, u.name AS writer_name FROM base_board b " +
            "JOIN users u ON b.user_id = u.user_id WHERE b.user_id = ? ORDER BY b.base_board_id DESC";

    final String SQL_UPDATE = "UPDATE base_board SET title = ?, content = ? WHERE base_board_id = ?";
    final String SQL_DELETE = "DELETE FROM base_board WHERE base_board_id = ?";

    // 1. 글 작성
    public int insertBoard(BoardDTO dto) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_INSERT);
            pstmt.setLong(1, dto.getUserId());
            pstmt.setString(2, dto.getTitle());
            pstmt.setString(3, dto.getContent());
            result = pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt); }
        return result;
    }

    // 2. [기존] 전체 목록 조회
    public ArrayList<BoardDTO> selectAllBoards() {
        return selectAllBoards(0, Integer.MAX_VALUE); // 아래 오버로딩 메서드 호출
    }

    // 2-1. [신규] 페이징 목록 조회 (offset: 시작 인덱스, limit: 가져올 개수)
    public ArrayList<BoardDTO> selectAllBoards(int offset, int limit) {
        ArrayList<BoardDTO> list = new ArrayList<>();
        con = JdbcConnectUtil.getConnection();
        try {
            // limit가 MAX_VALUE면 전체 조회 쿼리, 아니면 페이징 쿼리 사용 (또는 항상 페이징 쿼리 사용)
            if (limit == Integer.MAX_VALUE) {
                pstmt = con.prepareStatement(SQL_SELECT_ALL);
            } else {
                pstmt = con.prepareStatement(SQL_SELECT_PAGING);
                pstmt.setInt(1, offset); // 시작 위치 (0부터 시작)
                pstmt.setInt(2, limit);  // 개수
            }
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToBoard(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return list;
    }

    // 3. 상세 조회
    public BoardDTO selectBoardById(long boardId) {
        BoardDTO dto = null;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_SELECT_BY_ID);
            pstmt.setLong(1, boardId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = mapResultSetToBoard(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return dto;
    }

    // 4. 내 글 조회
    public ArrayList<BoardDTO> selectBoardsByUserId(long userId) {
        ArrayList<BoardDTO> list = new ArrayList<>();
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_SELECT_BY_USER_ID);
            pstmt.setLong(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToBoard(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt, rs); }
        return list;
    }

    // 5. 수정
    public int updateBoard(BoardDTO dto) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_UPDATE);
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setLong(3, dto.getBaseBoardId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt); }
        return result;
    }

    // 6. 삭제
    public int deleteBoard(long boardId) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_DELETE);
            pstmt.setLong(1, boardId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); } 
        finally { JdbcConnectUtil.close(con, pstmt); }
        return result;
    }

    private BoardDTO mapResultSetToBoard(ResultSet rs) throws SQLException {
        BoardDTO dto = new BoardDTO();
        dto.setBaseBoardId(rs.getLong("base_board_id"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        dto.setWriterName(rs.getString("writer_name"));
        return dto;
    }
}