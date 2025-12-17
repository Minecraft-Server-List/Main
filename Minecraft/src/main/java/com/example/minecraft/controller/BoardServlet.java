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

import com.example.minecraft.dto.BoardDTO;
import com.example.minecraft.service.BoardService;

@WebServlet("/board/*")
public class BoardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BoardService boardService;

    // 초기화: 서비스 객체 생성
    @Override
    public void init() throws ServletException {
        this.boardService = new BoardService();
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

        // Service 호출
        List<BoardDTO> boardList = boardService.getBoardListService(category, page);
        
        request.setAttribute("boardList", boardList);
        request.setAttribute("currentPage", page);
        request.setAttribute("currentCategory", category); 
        request.setAttribute("categoryName", getCategoryName(category)); 
        
        request.getRequestDispatcher("/WEB-INF/views/board/boardList.jsp").forward(request, response);
    }

    // [추가] 내 글 목록 조회
    private void listMyBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        long userId = getLoginUserId(request);
        if (userId == 0) {
            response.getWriter().write("<div style='padding:50px; text-align:center;'>로그인이 필요합니다.</div>");
            return;
        }

        // Service 호출
        List<BoardDTO> myBoardList = boardService.getMyBoardListService(userId);
        
        request.setAttribute("dataList", myBoardList); 
        request.setAttribute("currentType", "posts"); 
        
        request.getRequestDispatcher("/WEB-INF/views/mypage/mypageList.jsp").forward(request, response);
    }

    // 2. 상세 조회
 // [BoardServlet.java] viewBoard 메서드 수정
    private void viewBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        // [디버깅] 콘솔에 ID가 찍히는지 확인하세요!
        System.out.println(">>> 상세보기 요청 들어옴. ID 파라미터 값: " + idParam);

        if (idParam != null && !idParam.isEmpty()) {
            try {
                long boardId = Long.parseLong(idParam);
                long currentUserId = getLoginUserId(request);
                
                BoardDTO board = boardService.getBoardViewService(boardId, currentUserId);
                
                // [디버깅] DB에서 가져온 결과 확인
                if (board == null) {
                    System.out.println(">>> DB 조회 결과: NULL (DAO 에러 발생 가능성 있음)");
                } else {
                    System.out.println(">>> DB 조회 성공: " + board.getTitle());
                }

                request.setAttribute("board", board);
            } catch (Exception e) {
                System.out.println(">>> 에러 발생: ");
                e.printStackTrace(); // 콘솔에 빨간 에러 메시지를 꼭 확인해야 합니다.
            }
        } else {
            System.out.println(">>> ID 파라미터가 널(Null)이거나 비어있습니다.");
        }
        request.getRequestDispatcher("/WEB-INF/views/board/boardPost.jsp").forward(request, response);
    }

    // 3. 저장/수정
    private void saveBoard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        long userId = getLoginUserId(request);
        String userRole = getLoginUserRole(request);

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
            dto.setBaseBoardId(Long.parseLong(idParam));
        }

        // Service 호출
        boardService.saveBoardService(dto, isUpdate);

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
        
        // 권한 확인을 위해 게시글 정보 먼저 가져오기 (Service 호출)
        BoardDTO board = boardService.getBoardByIdService(boardId, userId);
        
        if (board == null) {
            out.print("{\"status\":\"fail\", \"message\":\"게시글이 없습니다.\"}");
            return;
        }

        if (board.getUserId() == userId || "ADMIN".equals(userRole)) {
            // Service 호출 (삭제)
            boardService.deleteBoardService(boardId);
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
        
        // Service 호출
        boolean liked = boardService.toggleLikeService(userId, boardId);
        
        out.print(String.format("{\"status\":\"success\", \"liked\": %b}", liked));
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