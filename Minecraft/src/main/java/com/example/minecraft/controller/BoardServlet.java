package com.example.minecraft.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.minecraft.dao.BoardDAO;
import com.example.minecraft.dto.BoardDTO;

@WebServlet("/board/*")
public class BoardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BoardDAO boardDAO;

    public BoardServlet() {
        boardDAO = new BoardDAO();
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
        if (action == null || action.equals("/")) action = "/list";

        try {
            switch (action) {
                case "/list": listBoard(request, response); break;
                case "/view": viewBoard(request, response); break;
                case "/save": saveBoard(request, response); break;
                case "/delete": deleteBoard(request, response); break;
                case "/like": toggleLike(request, response); break;
                
                // 마이페이지 내 글 목록
                case "/myList": listMyBoard(request, response); break; 
                
                default: response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 1. 전체 목록 조회
    private void listBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pageStr = request.getParameter("page");
        String category = request.getParameter("category");
        
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try { page = Integer.parseInt(pageStr); } catch (NumberFormatException e) {}
        }
        
        if (category == null || category.trim().isEmpty()) category = "ALL";

        int limit = 10;
        int offset = (page - 1) * limit;

        List<BoardDTO> boardList;
        if ("ALL".equals(category)) {
            boardList = boardDAO.selectAllBoards(offset, limit);
        } else {
            boardList = boardDAO.selectBoardsByCategory(category, offset, limit);
        }
        
        request.setAttribute("boardList", boardList);
        request.setAttribute("currentPage", page);
        request.setAttribute("currentCategory", category); 
        request.setAttribute("categoryName", getCategoryName(category)); 
        
        request.getRequestDispatcher("/WEB-INF/views/boardList.jsp").forward(request, response);
    }

    // [추가] 내 글 목록 조회 (mypageList.jsp 사용)
    private void listMyBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        long userId = getLoginUserId(request);
        if (userId == 0) {
            response.getWriter().write("<div style='padding:50px; text-align:center;'>로그인이 필요합니다.</div>");
            return;
        }

        // DAO 호출
        List<BoardDTO> myBoardList = boardDAO.selectBoardsByUserId(userId);
        
        // mypageList.jsp용 속성 설정
        request.setAttribute("dataList", myBoardList); 
        request.setAttribute("currentType", "posts"); 
        
        request.getRequestDispatcher("/WEB-INF/views/mypageList.jsp").forward(request, response);
    }

    // 2. 상세 조회
    private void viewBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            long boardId = Long.parseLong(idParam);
            long currentUserId = getLoginUserId(request);
            
            boardDAO.incrementViewCount(boardId);
            BoardDTO board = boardDAO.selectBoardById(boardId, currentUserId);
            request.setAttribute("board", board);
        }
        request.getRequestDispatcher("/WEB-INF/views/boardPost.jsp").forward(request, response);
    }

    // 3. 저장/수정
    private void saveBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        long userId = getLoginUserId(request);
        String userRole = getLoginUserRole(request); // 권한 확인

        if (userId == 0) {
            out.print("{\"status\":\"fail\", \"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        String idParam = request.getParameter("id");
        String category = request.getParameter("category"); 
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        if ((idParam == null || idParam.isEmpty()) && (category == null || category.isEmpty())) {
             out.print("{\"status\":\"fail\", \"message\":\"카테고리를 선택해야 합니다.\"}");
             return;
        }

        // [수정] 공지사항(NOTICE) 작성 권한 체크: 관리자(ADMIN)가 아니면 차단
        if ("NOTICE".equals(category) && !"ADMIN".equals(userRole)) {
            out.print("{\"status\":\"fail\", \"message\":\"공지사항은 관리자만 작성할 수 있습니다.\"}");
            return;
        }

        BoardDTO dto = new BoardDTO();
        dto.setUserId(userId);
        dto.setCategory(category); 
        dto.setTitle(title);
        dto.setContent(content);

        boolean isUpdate = (idParam != null && !idParam.isEmpty());
        
        if (isUpdate) {
            long boardId = Long.parseLong(idParam);
            dto.setBaseBoardId(boardId);
            boardDAO.updateBoard(dto);
        } else {
            boardDAO.insertBoard(dto);
        }

        out.print("{\"status\":\"success\", \"message\":\"저장되었습니다.\"}");
    }

    // 4. 삭제
    private void deleteBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        long userId = getLoginUserId(request);
        String userRole = getLoginUserRole(request);

        if (userId == 0) {
            out.print("{\"status\":\"fail\", \"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        long boardId = Long.parseLong(request.getParameter("id"));
        BoardDTO board = boardDAO.selectBoardById(boardId, userId);
        
        if (board == null) {
            out.print("{\"status\":\"fail\", \"message\":\"게시글이 없습니다.\"}");
            return;
        }

        if (board.getUserId() == userId || "ADMIN".equals(userRole)) {
            boardDAO.deleteBoard(boardId);
            out.print("{\"status\":\"success\"}");
        } else {
            out.print("{\"status\":\"fail\", \"message\":\"삭제 권한이 없습니다.\"}");
        }
    }

    // 5. 좋아요
    private void toggleLike(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        long userId = getLoginUserId(request);
        if (userId == 0) {
            out.print("{\"status\":\"fail\", \"message\":\"로그인이 필요합니다.\"}");
            return;
        }
        
        long boardId = Long.parseLong(request.getParameter("id"));
        int result = boardDAO.toggleLike(userId, boardId);
        
        out.print(String.format("{\"status\":\"success\", \"liked\": %b}", (result == 1)));
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
    
    private String getCategoryName(String code) {
        if(code == null) return "전체글보기";
        switch(code) {
            case "NOTICE": return "공지사항";
            case "GREETING": return "가입 인사";
            case "FREE": return "자유 게시판";
            case "PROMOTION": return "서버 홍보";
            case "QNA": return "질문/답변";
            case "SCRIPT": return "스크립트";
            default: return "전체글보기";
        }
    }
}