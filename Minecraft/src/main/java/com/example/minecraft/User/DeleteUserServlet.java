package com.example.minecraft.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/deleteUser.do")
public class DeleteUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Long.parseLong(request.getParameter("userId"));
        UserDTO userToDelete = dao.selectUserById(userId);
        int result = dao.deleteUser(userId);

        HttpSession session = request.getSession();
        if (result > 0 && userToDelete != null) {
            session.setAttribute("flash_deletedEmail", userToDelete.getEmail());
        } else {
            session.setAttribute("flash_message", "계정 삭제에 실패했습니다.");
        }
        
        response.sendRedirect("showResult.do");
    }
}