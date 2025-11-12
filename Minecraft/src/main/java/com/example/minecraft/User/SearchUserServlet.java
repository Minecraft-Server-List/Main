package com.example.minecraft.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/searchUser.do") // (JSP 폼의 action과 일치해야 함)
public class SearchUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET 방식으로 이메일 파라미터를 받음
        String emailToSearch = request.getParameter("email");
        
        UserDTO user = dao.selectUserByEmail(emailToSearch);

        if (user != null) {
            // Case 1: 검색 성공 -> DTO 전달
            request.setAttribute("userToEdit", user);
        } else {
            // Case 2: 검색 실패 -> message 전달
            request.setAttribute("message", "이메일(" + emailToSearch + ")과 일치하는 사용자가 없습니다.");
        }
        
        // singleUserResult.jsp로 포워딩
        RequestDispatcher dispatcher = request.getRequestDispatcher("singleUserResult.jsp");
        dispatcher.forward(request, response);
    }
}