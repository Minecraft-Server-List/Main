package com.example.minecraft.service;

import com.example.minecraft.dao.ComentDAO;
import com.example.minecraft.dto.ComentDTO;

import java.util.List;

public class ComentService {

    private final ComentDAO comentDAO = new ComentDAO();

    public List<ComentDTO> getCommentListService(long boardId, long currentUserId) {
        return comentDAO.selectComentsByBoardId(boardId, currentUserId);
    }

    public List<ComentDTO> getMyCommentListService(long userId) {
        return comentDAO.selectComentsByUserId(userId);
    }

    public void addCommentService(ComentDTO dto) {
        comentDAO.insertComent(dto);
    }

    public void deleteCommentService(long comentId) {
        comentDAO.deleteComent(comentId);
    }

    public boolean toggleLikeService(long userId, long comentId) {
        int result = comentDAO.toggleLike(userId, comentId);
        return result == 1;
    }
}