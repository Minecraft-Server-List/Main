package com.example.minecraft.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.example.minecraft.dao.UserDAO;
import com.example.minecraft.dto.UserDTO;

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [수정] index.jsp 직접 호출 불가 -> Controller 경유
        response.sendRedirect(request.getContextPath() + "/index.page");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String email = request.getParameter("id");
        String pass = request.getParameter("pw");
        
        UserDTO loginUser = dao.loginUser(email, pass);
        
        if (loginUser != null) {
            // --- 로그인 성공 ---
            HttpSession session = request.getSession();
            session.setAttribute("userName", loginUser.getName());
            session.setAttribute("userEmail", loginUser.getEmail());
            session.setAttribute("userRole", loginUser.getRole());
            
            // [수정] index.jsp 직접 호출 불가 -> Controller 경유
            response.sendRedirect(request.getContextPath() + "/index.page"); 
            
        } else {
            // --- 로그인 실패 ---
            request.setAttribute("message", "아이디(이메일) 또는 비밀번호를 확인해주세요.");
            
            // [수정] WEB-INF 내부 파일은 절대 경로로 지정해야 함
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/singleUserResult.jsp"); 
            dispatcher.forward(request, response);
        }
    }
}