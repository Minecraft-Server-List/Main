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
        
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);
        
     // 🚨 PageController에서 마이페이지 최초 진입 시 설정한 속성 확인 🚨
        boolean isMypageFirstLoad = (request.getAttribute("isMypage") != null);
        
        RequestDispatcher dispatcher;

        if (isAjax) {
            // 1. AJAX 요청 (마이페이지 탭 전환 시): Header/Footer 없는 Fragment 응답
            // 이 로직은 관리자 페이지의 AJAX 요청이 있다면 그대로 Fragment를 반환합니다.
            dispatcher = request.getRequestDispatcher("/WEB-INF/views/mypageEdit.jsp");
        }
        else {
            // 3. 관리자 기능 등 일반적인 페이지 이동 요청: 기존대로 singleUserResult.jsp 응답
            // (userList.do 등 다른 서블릿에서 searchUser.do로 요청된 경우)
            dispatcher = request.getRequestDispatcher("/WEB-INF/views/singleUserResult.jsp");
        }
        dispatcher.forward(request, response);
    }
}