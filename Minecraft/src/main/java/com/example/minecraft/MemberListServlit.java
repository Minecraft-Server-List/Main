package com.example.minecraft;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/memberList.do")
public class MemberListServlit extends HttpServlet {
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
        
        // request 객체에 "allList"라는 이름으로 데이터를 담음
        request.setAttribute("allList", aList);
        
        // memberList.jsp로 포워딩 (데이터 전달)
        RequestDispatcher dispatcher = request.getRequestDispatcher("memberList.jsp");
        dispatcher.forward(request, response);
    }
}