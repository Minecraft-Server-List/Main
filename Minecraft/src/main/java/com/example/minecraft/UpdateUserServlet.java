package com.example.minecraft;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 사용자 정보 수정을 처리하는 서블릿 (doPost 오버라이드)
 * 1. 폼에서 전달된 정보(userId, name, email, role, password)를 받습니다.
 * 2. DB 업데이트를 수행합니다.
 * 3. [핵심] 만약 수정된 사용자가 현재 로그인한 사용자와 동일하다면,
 * 세션에 저장된 로그인 정보(userName, userEmail, userRole)를 새 정보로 갱신합니다.
 * 4. 결과를 보여주기 위해 "showResult.do"로 리다이렉트합니다.
 */
@WebServlet("/updateUser.do")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        // 서블릿 초기화 시 UserDAO 인스턴스 생성
        dao = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 폼 데이터(hidden input 포함)에서 수정할 사용자의 ID를 가져옵니다.
        long userId = Long.parseLong(request.getParameter("userId"));
        
        // 2. 비교를 위해 DB에서 원본 사용자 데이터를 가져옵니다.
        UserDTO oldUser = dao.selectUserById(userId);
        
        // 3. (방어 코드) 수정할 사용자가 DB에 없는 경우
        if (oldUser == null) {
            HttpSession session = request.getSession();
            session.setAttribute("flash_message", "수정할 사용자 정보를 찾지 못했습니다.");
            response.sendRedirect("showResult.do");
            return; // 서블릿 실행 종료
        }

        // 4. 폼에서 받은 새 정보로 newUser DTO 객체를 생성합니다.
        UserDTO newUser = new UserDTO();
        newUser.setUserId(userId); // ID는 변경되지 않음
        newUser.setName(request.getParameter("name"));
        newUser.setEmail(request.getParameter("email"));
        newUser.setRole(request.getParameter("role"));

        // 5. 비밀번호 처리:
        // 새 비밀번호가 입력되었으면(null이나 빈 문자열이 아니면) 새 비밀번호로 설정하고,
        // 입력되지 않았으면 기존 비밀번호(oldUser.getPassword())를 그대로 유지합니다.
        String newPassword = request.getParameter("password");
        if (newPassword != null && !newPassword.isEmpty()) {
            newUser.setPassword(newPassword); 
        } else {
            newUser.setPassword(oldUser.getPassword());
        }

        // 6. DAO를 통해 DB 업데이트 실행
        int result = dao.updateUser(newUser);
        
        HttpSession session = request.getSession();
        
        // 7. DB 업데이트 성공 여부 분기
        if (result > 0) {
            // 7-1. [성공] 결과 페이지(singleUserResult.jsp)에 변경 전/후 데이터를 보여주기 위해 Flash 속성 설정
            session.setAttribute("flash_oldUserData", oldUser);
            session.setAttribute("flash_newUserData", newUser);

            // --- ▼▼▼ [핵심 수정 로직] ▼▼▼ ---
            
            // 7-2. 현재 로그인한 사용자의 이메일 (세션에서 가져오기)
            String loggedInUserEmail = (String) session.getAttribute("userEmail");

            // 7-3. 만약 지금 수정한 사용자(oldUser)가 현재 로그인한 사용자와 동일하다면,
            //      (이메일이 변경되었을 수 있으므로, 변경 전 이메일인 oldUser.getEmail()과 비교)
            if (loggedInUserEmail != null && loggedInUserEmail.equals(oldUser.getEmail())) {
                
                // 7-4. 세션에 저장된 로그인 정보를 새 정보(newUser)로 즉시 갱신!
                //      (header.jsp 등에서 사용하므로 중요)
                session.setAttribute("userName", newUser.getName());
                session.setAttribute("userEmail", newUser.getEmail()); // <-- 중요!
                session.setAttribute("userRole", newUser.getRole());
            }
            // --- ▲▲▲ [수정 완료] ▲▲▲ ---

        } else {
            // 7-5. [실패] 결과 페이지에 실패 메시지를 보내기 위해 Flash 속성 설정
            session.setAttribute("flash_message", "정보 수정에 실패했습니다.");
        }
        
        // 8. 처리가 완료되었으므로, 결과 페이지를 담당하는 서블릿으로 리다이렉트합니다.
        response.sendRedirect("showResult.do"); 
    }
}