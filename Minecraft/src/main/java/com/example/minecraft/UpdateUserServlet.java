package com.example.minecraft;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/updateUser.do")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Long.parseLong(request.getParameter("userId"));
        UserDTO oldUser = dao.selectUserById(userId);
        
        if (oldUser == null) {
            HttpSession session = request.getSession();
            session.setAttribute("flash_message", "수정할 사용자 정보를 찾지 못했습니다.");
            response.sendRedirect("showResult.do");
            return;
        }

        UserDTO newUser = new UserDTO();
        newUser.setUserId(userId);
        newUser.setName(request.getParameter("name"));
        newUser.setEmail(request.getParameter("email"));
        newUser.setRole(request.getParameter("role"));

        String newPassword = request.getParameter("password");
        if (newPassword != null && !newPassword.isEmpty()) {
            newUser.setPassword(newPassword); 
        } else {
            newUser.setPassword(oldUser.getPassword());
        }

        int result = dao.updateUser(newUser);
        
        HttpSession session = request.getSession();
        if (result > 0) {
            session.setAttribute("flash_oldUserData", oldUser);
            session.setAttribute("flash_newUserData", newUser);
        } else {
            session.setAttribute("flash_message", "정보 수정에 실패했습니다.");
        }
        
        response.sendRedirect("showResult.do"); 
    }
}