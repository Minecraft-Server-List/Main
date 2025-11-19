package com.example.minecraft.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.example.minecraft.dao.UserDAO;
import com.example.minecraft.dto.UserDTO;

@WebServlet("/searchUser.do")
public class SearchUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailToSearch = request.getParameter("email");
        
        UserDTO user = dao.selectUserByEmail(emailToSearch);

        if (user != null) {
            request.setAttribute("userToEdit", user);
        } else {
            request.setAttribute("message", "이메일(" + emailToSearch + ")과 일치하는 사용자가 없습니다.");
        }
        
        // [수정] WEB-INF 내부 파일 경로로 정확하게 지정
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/singleUserResult.jsp");
        dispatcher.forward(request, response);
    }
}