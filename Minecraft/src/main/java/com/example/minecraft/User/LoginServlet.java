package com.example.minecraft.User; // 1. 패키지명은 본인 것에 맞게 유지

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO(); // DAO를 init에서 한 번만 생성 (좋은 방식입니다)
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET 요청 시 (주소창에 직접 입력 등) index.jsp로 돌려보냄
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String email = request.getParameter("id");
        String pass = request.getParameter("pw");
        
        // [효율화]
        // loginCheck()와 selectUserByEmail()을 두 번 호출하는 대신,
        // 로그인과 정보 조회를 한 번에 처리하는 메서드(loginUser)를 호출합니다.
        // (UserDAO.java에 이 메서드가 필요합니다. 아래 설명 참조)
        UserDTO loginUser = dao.loginUser(email, pass);
        
        if (loginUser != null) {
            // --- 로그인 성공 ---
            
            // 1. 세션에 사용자 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("userName", loginUser.getName());
            session.setAttribute("userEmail", loginUser.getEmail());
            session.setAttribute("userRole", loginUser.getRole());
            
            // 2. [수정] loginOk.jsp 대신 index.jsp로 리다이렉트
            response.sendRedirect("index.jsp"); 
            
        } else {
            // --- 로그인 실패 ---
            
            // 1. [수정] request에 에러 메시지 저장
            request.setAttribute("message", "아이디(이메일) 또는 비밀번호를 확인해주세요.");
            
            // 2. [수정] loginFail.jsp 대신 userManageResult.jsp로 포워드
            RequestDispatcher dispatcher = request.getRequestDispatcher("singleUserResult.jsp"); 
            dispatcher.forward(request, response);
        }
    }
}