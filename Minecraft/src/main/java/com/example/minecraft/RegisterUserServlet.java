package com.example.minecraft;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/register.do")
public class RegisterUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET 요청 시 폼 페이지로 보냄
        response.sendRedirect("registerForm.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 폼 데이터 받기
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); 
        // (보안 경고: 실제로는 여기서 password를 해시(hash)해야 합니다)

        // 2. DTO 생성 및 데이터 설정
        UserDTO dto = new UserDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPassword(password);
        
        // 3. [중요] 역할(Role)은 "USER"로 강제 할당
        dto.setRole("USER"); 

        // 4. DAO를 통해 DB에 삽입 시도
        int result = 0;
        try {
            result = dao.insertUser(dto);
        } catch (Exception e) {
            // (예외 처리) DB 제약 조건 위반 (예: 이메일 중복) 등
            e.printStackTrace();
        }

        // 5. PRG 패턴을 위해 session에 결과 메시지 저장
        HttpSession session = request.getSession();
        if (result > 0) {
            // [성공]
            session.setAttribute("flash_message", "회원가입이 완료되었습니다. 로그인해주세요.");
        } else {
            // [실패] (이메일 중복이 가장 흔한 원인)
            session.setAttribute("flash_message", "회원가입에 실패했습니다. (이메일 중복 등)");
        }
        
        // 6. 결과 페이지 서블릿으로 리다이렉트
        response.sendRedirect("showResult.do");
    }
}