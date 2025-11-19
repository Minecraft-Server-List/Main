package com.example.minecraft.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.example.minecraft.dao.UserDAO;
import com.example.minecraft.dto.UserDTO;

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
        // [수정] WEB-INF/views/registerForm.jsp는 직접 접근 불가하므로 PageController 이용
        response.sendRedirect(request.getContextPath() + "/register.page");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); 
        
        UserDTO dto = new UserDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole("USER"); 

        int result = 0;
        try {
            result = dao.insertUser(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpSession session = request.getSession();
        if (result > 0) {
            session.setAttribute("flash_message", "회원가입이 완료되었습니다. 로그인해주세요.");
        } else {
            session.setAttribute("flash_message", "회원가입에 실패했습니다. (이메일 중복 등)");
        }
        
        // [확인 필요] showResult.do가 존재한다면 유지, 없다면 singleResult.page 등으로 변경 필요
        // 일단 기존에 showResult.do 서블릿이 있다고 가정하고 유지합니다.
        response.sendRedirect("showResult.do");
    }
}