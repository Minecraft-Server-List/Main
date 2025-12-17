package com.example.minecraft.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.minecraft.dto.ComentDTO;
import com.example.minecraft.service.ComentService;

@WebServlet("/comment/*")
public class ComentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ComentService comentService;

    @Override
    public void init() throws ServletException {
        this.comentService = new ComentService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        
        try {
            switch (action) {
                case "/list": 
                    response.setContentType("application/json;charset=UTF-8");
                    listComments(request, response); 
                    break;
                case "/add": 
                    response.setContentType("application/json;charset=UTF-8");
                    addComment(request, response); 
                    break;
                case "/delete": 
                    response.setContentType("application/json;charset=UTF-8");
                    deleteComment(request, response); 
                    break;
                case "/like": 
                    response.setContentType("application/json;charset=UTF-8");
                    toggleLike(request, response); 
                    break;
                
                // 내 댓글 목록 (HTML 반환)
                case "/myList": 
                    listMyComments(request, response); 
                    break;
                    
                default: 
                    response.getWriter().print("{\"status\":\"error\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("{\"status\":\"error\"}");
        }
    }

    private void listMyComments(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        long userId = getLoginUserId(request);
        if (userId == 0) {
            response.getWriter().write("<div style='padding:50px; text-align:center;'>로그인이 필요합니다.</div>");
            return;
        }

        // Service 호출
        List<ComentDTO> myCommentList = comentService.getMyCommentListService(userId);
        
        request.setAttribute("dataList", myCommentList); 
        request.setAttribute("currentType", "comments"); 
        
        request.getRequestDispatcher("/WEB-INF/views/mypage/mypageList.jsp").forward(request, response);
    }

    private void listComments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long boardId = Long.parseLong(request.getParameter("bid"));
        long currentUserId = getLoginUserId(request);
        String userRole = getLoginUserRole(request);

        // Service 호출
        List<ComentDTO> list = comentService.getCommentListService(boardId, currentUserId);
        
        StringBuilder json = new StringBuilder("[");
        for(int i=0; i<list.size(); i++) {
            ComentDTO c = list.get(i);
            json.append(String.format(
                "{\"cid\":%d, \"writerId\":%d, \"writer\":\"%s\", \"content\":\"%s\", \"date\":\"%s\", \"likes\":%d, \"isLiked\":%b, \"currentUserRole\":\"%s\"}",
                c.getComentId(),
                c.getUserId(),
                escapeJson(c.getWriterName()), 
                escapeJson(c.getContent()), 
                c.getCreatedAt(), 
                c.getLikeCount(),
                c.isLiked(),
                userRole
            ));
            if(i < list.size() - 1) json.append(",");
        }
        json.append("]");
        response.getWriter().print(json.toString());
    }

    private void addComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long userId = getLoginUserId(request);
        if (userId == 0) {
            response.getWriter().print("{\"status\":\"fail\", \"message\":\"로그인이 필요합니다.\"}");
            return;
        }
        long baseBoardId = Long.parseLong(request.getParameter("base_board_id"));
        String content = request.getParameter("content");

        ComentDTO dto = new ComentDTO();
        dto.setUserId(userId);
        dto.setBaseBoardId(baseBoardId);
        dto.setContent(content);

        // Service 호출
        comentService.addCommentService(dto);
        response.getWriter().print("{\"status\":\"success\"}");
    }
    
    private void deleteComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long userId = getLoginUserId(request);
        if (userId == 0) {
            response.getWriter().print("{\"status\":\"fail\", \"message\":\"로그인이 필요합니다.\"}");
            return;
        }
        long cid = Long.parseLong(request.getParameter("coment_id"));
        
        // Service 호출
        comentService.deleteCommentService(cid);
        response.getWriter().print("{\"status\":\"success\"}");
    }

    private void toggleLike(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long userId = getLoginUserId(request);
        if (userId == 0) {
            response.getWriter().print("{\"status\":\"fail\", \"message\":\"로그인 필요\"}");
            return;
        }
        long cid = Long.parseLong(request.getParameter("id"));
        
        // Service 호출
        boolean liked = comentService.toggleLikeService(userId, cid);
        
        response.getWriter().print(String.format("{\"status\":\"success\", \"liked\": %b}", liked));
    }

    private long getLoginUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return Long.parseLong(session.getAttribute("userId").toString());
        }
        return 0;
    }
    
    private String getLoginUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userRole") != null) {
            return (String) session.getAttribute("userRole");
        }
        return "";
    }

    private String escapeJson(String text) {
        if(text == null) return "";
        return text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}