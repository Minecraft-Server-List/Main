package com.example.minecraft.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/showResult.do")
public class ShowResultServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // 1. 세션(Flash) -> request로 데이터 이동 및 세션 청소
        if (session.getAttribute("flash_oldUserData") != null) {
            request.setAttribute("oldUserData", session.getAttribute("flash_oldUserData"));
            request.setAttribute("newUserData", session.getAttribute("flash_newUserData"));
            session.removeAttribute("flash_oldUserData");
            session.removeAttribute("flash_newUserData");
        }
        
        if (session.getAttribute("flash_deletedEmail") != null) {
            request.setAttribute("deletedEmail", session.getAttribute("flash_deletedEmail"));
            session.removeAttribute("flash_deletedEmail");
        }

        if (session.getAttribute("flash_message") != null) {
            request.setAttribute("message", session.getAttribute("flash_message"));
            session.removeAttribute("flash_message");
        }

        // [수정] WEB-INF 내부의 절대 경로로 변경
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/singleUserResult.jsp");
        dispatcher.forward(request, response);
    }
}