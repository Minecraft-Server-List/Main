package com.example.minecraft.util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 1. URL 매핑: 주소가 ".page"로 끝나는 모든 요청을 이 서블릿이 낚아챕니다.
@WebServlet("*.page")
public class PageController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    // GET이든 POST든 이 메서드에서 처리합니다.
    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 2. 사용자가 요청한 주소(URI) 분석
        String uri = request.getRequestURI(); 
        String conPath = request.getContextPath(); 
        String command = uri.substring(conPath.length()); 
        
        String viewPage = null;

        // 3. 요청에 따라 보여줄 JSP 파일(목적지) 설정
        // (첫 번째 코드의 경로 변경 사항을 반영함)
        switch (command) {
            case "/login.page":
                // 로그인 화면: /views/user/ 폴더로 변경됨
                viewPage = "/WEB-INF/views/user/login.jsp";
                break;
                
            case "/register.page":
                // 회원가입 화면: /views/user/ 폴더로 변경됨
                viewPage = "/WEB-INF/views/user/registerForm.jsp";
                break;
                
            case "/index.page":
                viewPage = "/index.jsp";
                break;
                
            case "/mypage.page":
                String userEmail = (String) request.getSession().getAttribute("userEmail");
                if (userEmail != null) {
                    // 마이페이지: /views/mypage/ 폴더로 변경됨
                    viewPage = "/WEB-INF/views/mypage/mypageMain.jsp";
                } else {
                    viewPage = "/WEB-INF/views/user/login.jsp";
                }
                break;
                
            case "/board.page":
                // 게시판: /views/board/ 폴더로 변경됨
                viewPage = "/WEB-INF/views/board/boardMain.jsp";
                break;
                
            case "/serverAdd.page":
                viewPage = "/WEB-INF/views/server/form.jsp";
                break;
                
            case "/serverList.page":
                // 데이터 처리가 필요한 경우 다른 서블릿으로 포워딩
                request.getRequestDispatcher("/serverList").forward(request, response);
                return; // 여기서 종료
        }

        // 4. 목적지로 포워딩 (화면 이동)
        if (viewPage != null) {
            request.getRequestDispatcher(viewPage).forward(request, response);
        } else {
            // 매핑된 페이지가 없을 경우 메인으로 보냄
            response.sendRedirect(conPath + "/index.jsp");
        }
    }
}