package com.example.minecraft.service;

import com.example.minecraft.dao.BoardDAO;
import com.example.minecraft.dto.BoardDTO;

import java.util.List;

public class BoardService {

    private final BoardDAO boardDAO = new BoardDAO();

    // 1. 전체 목록 조회 (페이징, 카테고리)
    public List<BoardDTO> getBoardListService(String category, int page) {
        int limit = 10;
        int offset = (page - 1) * limit;

        if ("ALL".equals(category)) {
            return boardDAO.selectAllBoards(offset, limit);
        } else {
            return boardDAO.selectBoardsByCategory(category, offset, limit);
        }
    }

    // 2. 내 글 목록 조회
    public List<BoardDTO> getMyBoardListService(long userId) {
        return boardDAO.selectBoardsByUserId(userId);
    }

    // 3. 상세 조회 (조회수 증가 + 조회)
    public BoardDTO getBoardViewService(long boardId, long currentUserId) {
        boardDAO.incrementViewCount(boardId);
        return boardDAO.selectBoardById(boardId, currentUserId);
    }

    // 3-1. 단순 조회 (삭제/수정 권한 확인용)
    public BoardDTO getBoardByIdService(long boardId, long currentUserId) {
        return boardDAO.selectBoardById(boardId, currentUserId);
    }

    // 4. 저장 및 수정
    public void saveBoardService(BoardDTO dto, boolean isUpdate) {
        if (isUpdate) {
            boardDAO.updateBoard(dto);
        } else {
            boardDAO.insertBoard(dto);
        }
    }

    // 5. 삭제
    public boolean deleteBoardService(long boardId) {
        int result = boardDAO.deleteBoard(boardId);
        return result == 1;
    }

    // 6. 좋아요 토글
    public boolean toggleLikeService(long userId, long boardId) {
        int result = boardDAO.toggleLike(userId, boardId);
        return result == 1;
    }
}