package com.example.minecraft;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login.do")
public class LoginServlit extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO(); // DAO를 init에서 한 번만 생성
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET 요청 시 (주소창에 직접 입력 등) index.jsp로 돌려보냄
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // POST 요청 (로그인 폼 제출) 처리
        String email = request.getParameter("id");
        String pass = request.getParameter("pw");
        
        UserDTO dtoToLogin = new UserDTO();
        dtoToLogin.setEmail(email);
        dtoToLogin.setPassword(pass);
        
        boolean result = dao.loginCheck(dtoToLogin);
        
        if (result) {
            // --- 로그인 성공 ---
            HttpSession session = request.getSession();
            UserDTO loginUser = dao.selectUserByEmail(email); // 로그인한 사용자 정보 조회
            
            if (loginUser != null) {
                session.setAttribute("userName", loginUser.getName());
                session.setAttribute("userEmail", loginUser.getEmail());
                session.setAttribute("userRole", loginUser.getRole());
            }
            response.sendRedirect("loginOk.jsp"); // 성공 페이지로 이동
        } else {
            // --- 로그인 실패 ---
            // (loginFail.jsp가 별도로 없다면 index.jsp로 보낼 수 있습니다)
            RequestDispatcher dispatcher = request.getRequestDispatcher("loginFail.jsp"); 
            dispatcher.forward(request, response);
        }
    }
}