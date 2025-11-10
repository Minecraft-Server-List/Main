package com.example.minecraft;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout.do")
public class LogoutServlit extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET 요청 (링크 클릭)으로 로그아웃 처리
        HttpSession session = request.getSession(false); // 기존 세션 가져오기
        
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        
        response.sendRedirect("index.jsp"); // 홈으로 리다이렉트
    }
}