package com.example.minecraft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.minecraft.Util.JdbcConnectUtil;
import com.example.minecraft.dto.ComentDTO;

public class ComentDAO {
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // ============================================================
    // SQL 쿼리 정의
    // ============================================================

    // 1. 댓글 작성 (like 컬럼은 별도 테이블로 분리되어 제거됨)
    final String SQL_INSERT = "INSERT INTO coment (user_id, base_board_id, content) VALUES (?, ?, ?)";
    
    // 2. 게시글 상세 페이지용 댓글 조회
    // - like_count: comment_likes 테이블에서 해당 댓글의 총 개수 계산
    // - is_liked: 현재 로그인한 유저(?)가 좋아요를 눌렀는지 확인 (1이면 True, 0이면 False)
    final String SQL_SELECT_BY_BOARD = 
            "SELECT c.*, u.name AS writer_name, " +
            "(SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id) AS like_count, " +
            "(SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id AND cl.user_id = ?) AS is_liked " +
            "FROM coment c " +
            "JOIN users u ON c.user_id = u.user_id " +
            "WHERE c.base_board_id = ? " +
            "ORDER BY c.coment_id ASC";

    // 3. 마이페이지용 댓글 조회 (로직 수정됨)
    // - 내가 쓴 댓글을 가져오되, '내가 내 댓글에 좋아요를 눌렀는지'도 정확히 체크하도록 수정
    // - 게시글 제목(board_title) 조인 포함
    final String SQL_SELECT_BY_USER = 
            "SELECT c.*, b.title AS board_title, " +
            "(SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id) AS like_count, " +
            "(SELECT COUNT(*) FROM comment_likes cl WHERE cl.coment_id = c.coment_id AND cl.user_id = ?) AS is_liked " +
            "FROM coment c " +
            "JOIN base_board b ON c.base_board_id = b.base_board_id " +
            "WHERE c.user_id = ? " +
            "ORDER BY c.coment_id DESC";

    // 4. 댓글 수정 (내용만 수정, 수정일자는 DB 트리거 혹은 자동갱신 설정 가정)
    final String SQL_UPDATE = "UPDATE coment SET content = ? WHERE coment_id = ?";

    // 5. 댓글 삭제
    final String SQL_DELETE = "DELETE FROM coment WHERE coment_id = ?";
    
    // 6. 좋아요 관련 쿼리
    final String SQL_CHECK_LIKE = "SELECT 1 FROM comment_likes WHERE user_id = ? AND coment_id = ?";
    final String SQL_ADD_LIKE = "INSERT INTO comment_likes (user_id, coment_id) VALUES (?, ?)";
    final String SQL_REMOVE_LIKE = "DELETE FROM comment_likes WHERE user_id = ? AND coment_id = ?";


    // ============================================================
    // 메서드 구현
    // ============================================================

    /**
     * [작성] 댓글 등록
     */
    public int insertComent(ComentDTO dto) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_INSERT);
            pstmt.setLong(1, dto.getUserId());
            pstmt.setLong(2, dto.getBaseBoardId());
            pstmt.setString(3, dto.getContent());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
        }
        return result;
    }

    /**
     * [수정] 댓글 내용 수정 (누락된 기능 추가)
     */
    public int updateComent(long comentId, String content) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_UPDATE);
            pstmt.setString(1, content);
            pstmt.setLong(2, comentId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
        }
        return result;
    }

    /**
     * [조회] 특정 게시글의 댓글 목록 (좋아요 정보 포함)
     * @param boardId 조회할 게시글 ID
     * @param currentUserId 현재 로그인한 유저 ID (좋아요 여부 체크용)
     */
    public ArrayList<ComentDTO> selectComentsByBoardId(long boardId, long currentUserId) {
        ArrayList<ComentDTO> list = new ArrayList<>();
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_SELECT_BY_BOARD);
            pstmt.setLong(1, currentUserId); // 첫 번째 ? : is_liked 서브쿼리용
            pstmt.setLong(2, boardId);       // 두 번째 ? : 게시글 ID
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ComentDTO dto = mapResultSetToComent(rs);
                // JOIN된 추가 정보 매핑
                dto.setWriterName(rs.getString("writer_name"));
                
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return list;
    }

    /**
     * [마이페이지] 내가 쓴 댓글 목록 (게시글 제목 + 좋아요 정보 포함)
     * @param userId 내 유저 ID
     */
    public ArrayList<ComentDTO> selectComentsByUserId(long userId) {
        ArrayList<ComentDTO> list = new ArrayList<>();
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_SELECT_BY_USER);
            // 중요: 파라미터를 2번 세팅해야 합니다.
            pstmt.setLong(1, userId); // 첫 번째 ? : is_liked 서브쿼리용 (내가 좋아요 눌렀는지)
            pstmt.setLong(2, userId); // 두 번째 ? : WHERE c.user_id (내 댓글 찾기)
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ComentDTO dto = mapResultSetToComent(rs);
                // JOIN된 추가 정보 매핑
                dto.setBoardTitle(rs.getString("board_title"));
                
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return list;
    }

    /**
     * [삭제] 댓글 삭제
     */
    public int deleteComent(long comentId) {
        int result = 0;
        con = JdbcConnectUtil.getConnection();
        try {
            pstmt = con.prepareStatement(SQL_DELETE);
            pstmt.setLong(1, comentId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcConnectUtil.close(con, pstmt);
        }
        return result;
    }

    /**
     * [기능] 좋아요 토글 (트랜잭션 적용됨)
     * - 이미 좋아요가 있으면 삭제(취소)하고 0 반환
     * - 없으면 추가하고 1 반환
     */
    public int toggleLike(long userId, long comentId) {
        int status = -1; // -1: 에러, 0: 취소됨, 1: 추가됨
        con = JdbcConnectUtil.getConnection();
        
        try {
            // 1. 트랜잭션 시작
            con.setAutoCommit(false);

            // 2. 현재 좋아요 상태 확인
            pstmt = con.prepareStatement(SQL_CHECK_LIKE);
            pstmt.setLong(1, userId);
            pstmt.setLong(2, comentId);
            rs = pstmt.executeQuery();
            
            boolean exists = rs.next();
            
            // 리소스 정리 (다음 쿼리 준비)
            JdbcConnectUtil.close(null, pstmt, rs);

            if (exists) {
                // 3-A. 이미 존재하면 삭제 (취소)
                pstmt = con.prepareStatement(SQL_REMOVE_LIKE);
                pstmt.setLong(1, userId);
                pstmt.setLong(2, comentId);
                pstmt.executeUpdate();
                status = 0;
            } else {
                // 3-B. 없으면 삽입 (추가)
                pstmt = con.prepareStatement(SQL_ADD_LIKE);
                pstmt.setLong(1, userId);
                pstmt.setLong(2, comentId);
                pstmt.executeUpdate();
                status = 1;
            }

            // 4. 커밋
            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // 에러 발생 시 롤백
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 5. AutoCommit 원복 및 자원 해제
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
            JdbcConnectUtil.close(con, pstmt, rs);
        }
        return status;
    }

    // --- Helper Method: ResultSet -> DTO 매핑 ---
    private ComentDTO mapResultSetToComent(ResultSet rs) throws SQLException {
        ComentDTO dto = new ComentDTO();
        dto.setComentId(rs.getLong("coment_id"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setBaseBoardId(rs.getLong("base_board_id"));
        dto.setContent(rs.getString("content"));
        dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        dto.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class)); // 수정일 추가
        
        // 좋아요 관련 매핑 (서브쿼리 결과)
        dto.setLikeCount(rs.getInt("like_count"));
        dto.setLiked(rs.getInt("is_liked") > 0); // 1보다 크면 true
        
        return dto;
    }
}