package com.example.minecraft.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import com.example.minecraft.dao.UserDAO;
import com.example.minecraft.dto.UserDTO;

@WebServlet("/userList.do")
public class UserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // DB에서 전체 리스트를 가져옴
        ArrayList<UserDTO> aList = dao.SelectAll();
        
        request.setAttribute("allList", aList);
        
        // [수정] WEB-INF 내부의 절대 경로로 변경
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/userList.jsp");
        dispatcher.forward(request, response);
    }
}