package com.example.minecraft.controller;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.minecraft.dto.UserDTO;
import com.example.minecraft.service.UserService;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        if (action == null || action.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            switch (action) {
                case "/login": handleLogin(request, response); break;
                case "/logout": handleLogout(request, response); break;
                case "/register": registerUser(request, response); break;
                case "/list": listUsers(request, response); break;
                case "/search": searchUser(request, response); break;
                case "/update": updateUser(request, response); break;
                case "/delete": deleteUser(request, response); break;
                case "/result": showResult(request, response); break;
                default: response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 1. 로그인 처리
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            response.sendRedirect(request.getContextPath() + "/index.page");
            return;
        }

        String email = request.getParameter("id");
        String pass = request.getParameter("pw");
        
        // Service 호출
        UserDTO loginUser = userService.loginService(email, pass);
        
        if (loginUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userName", loginUser.getName());
            session.setAttribute("userId", loginUser.getUserId());
            session.setAttribute("userEmail", loginUser.getEmail());
            session.setAttribute("userRole", loginUser.getRole());
            
            response.sendRedirect(request.getContextPath() + "/index.page");
        } else {
            request.setAttribute("message", "아이디(이메일) 또는 비밀번호를 확인해주세요.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/user/singleUserResult.jsp");
            dispatcher.forward(request, response);
        }
    }

    // 2. 로그아웃 처리
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/index.page");
    }

    // 3. 회원가입 처리
    private void registerUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            response.sendRedirect(request.getContextPath() + "/register.page");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); 
        
        UserDTO dto = new UserDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole("USER"); 

        // Service 호출
        boolean success = userService.registerService(dto);

        HttpSession session = request.getSession();
        if (success) {
            session.setAttribute("flash_message", "회원가입이 완료되었습니다. 로그인해주세요.");
        } else {
            session.setAttribute("flash_message", "회원가입에 실패했습니다. (이메일 중복 등)");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/result");
    }

    // 4. 전체 회원 목록
    private void listUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Service 호출
        ArrayList<UserDTO> aList = userService.getAllUsersService();
        request.setAttribute("allList", aList);
        request.getRequestDispatcher("/WEB-INF/views/user/userList.jsp").forward(request, response);
    }

    // 5. 회원 검색
    private void searchUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String emailToSearch = request.getParameter("email");
        
        // Service 호출
        UserDTO user = userService.getUserByEmailService(emailToSearch);

        if (user != null) {
            request.setAttribute("userToEdit", user);
        } else {
            request.setAttribute("message", "이메일(" + emailToSearch + ")과 일치하는 사용자가 없습니다.");
        }
        
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);
        
        if (isAjax) {
            request.getRequestDispatcher("/WEB-INF/views/mypage/mypageEdit.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/user/singleUserResult.jsp").forward(request, response);
        }
    }

    // 6. 회원 정보 수정
    private void updateUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        long userId = Long.parseLong(request.getParameter("userId"));
        
        // Service 호출 (기존 정보 가져오기)
        UserDTO oldUser = userService.getUserByIdService(userId);
        HttpSession session = request.getSession();
        
        if (oldUser == null) {
            session.setAttribute("flash_message", "수정할 사용자 정보를 찾지 못했습니다.");
            response.sendRedirect(request.getContextPath() + "/user/result");
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

        // Service 호출 (수정 실행)
        boolean success = userService.updateUserService(newUser);
        
        if (success) {
            session.setAttribute("flash_oldUserData", oldUser);
            session.setAttribute("flash_newUserData", newUser);
            
            // 세션 갱신 로직 (Controller 책임)
            String loggedInUserEmail = (String) session.getAttribute("userEmail");
            if (loggedInUserEmail != null && loggedInUserEmail.equals(oldUser.getEmail())) {
                session.setAttribute("userName", newUser.getName());
                session.setAttribute("userEmail", newUser.getEmail());
                session.setAttribute("userRole", newUser.getRole());
            }
        } else {
            session.setAttribute("flash_message", "정보 수정에 실패했습니다.");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/result");
    }

    // 7. 회원 삭제
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        long userId = Long.parseLong(request.getParameter("userId"));
        
        // Service 호출 (삭제할 유저 정보 확보)
        UserDTO userToDelete = userService.getUserByIdService(userId);
        
        // Service 호출 (삭제 실행)
        boolean success = userService.deleteUserService(userId);

        HttpSession session = request.getSession();
        if (success && userToDelete != null) {
            session.setAttribute("flash_deletedEmail", userToDelete.getEmail());
        } else {
            session.setAttribute("flash_message", "계정 삭제에 실패했습니다.");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/result");
    }

    // 8. 결과 화면 처리
    private void showResult(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        if (session.getAttribute("flash_oldUserData") != null) {
            request.setAttribute("oldUserData", session.getAttribute("flash_oldUserData"));
            request.setAttribute("newUserData", session.getAttribute("flash_newUserData"));
            session.removeAttribute("flash_oldUserData");
            session.removeAttribute("flash_newUserData");
        }
        
        if (session.getAttribute("flash_deletedEmail") != null) {
            request.setAttribute("deletedEmail", session.getAttribute("flash_deletedEmail"));
            session.removeAttribute("flash_deletedEmail");
        }

        if (session.getAttribute("flash_message") != null) {
            request.setAttribute("message", session.getAttribute("flash_message"));
            session.removeAttribute("flash_message");
        }

        request.getRequestDispatcher("/WEB-INF/views/user/singleUserResult.jsp").forward(request, response);
    }
}