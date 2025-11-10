package com.example.minecraft;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/showResult.do")
public class ShowResultServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // 1. 세션(Flash)에 있던 "수정 결과"를 request로 옮김
        if (session.getAttribute("flash_oldUserData") != null) {
            request.setAttribute("oldUserData", session.getAttribute("flash_oldUserData"));
            request.setAttribute("newUserData", session.getAttribute("flash_newUserData"));
            
            // 2. 세션에서 즉시 삭제 (소멸)
            session.removeAttribute("flash_oldUserData");
            session.removeAttribute("flash_newUserData");
        }
        
        // 3. 세션(Flash)에 있던 "삭제 결과"를 request로 옮김
        if (session.getAttribute("flash_deletedEmail") != null) {
            request.setAttribute("deletedEmail", session.getAttribute("flash_deletedEmail"));
            
            // 4. 세션에서 즉시 삭제 (소멸)
            session.removeAttribute("flash_deletedEmail");
        }

        // 5. 세션(Flash)에 있던 "기타 메시지"를 request로 옮김
        if (session.getAttribute("flash_message") != null) {
            request.setAttribute("message", session.getAttribute("flash_message"));
            
            // 6. 세션에서 즉시 삭제 (소멸)
            session.removeAttribute("flash_message");
        }

        // 7. singleUserResult.jsp로 포워딩 (이제 이 JSP는 안전함)
        RequestDispatcher dispatcher = request.getRequestDispatcher("singleUserResult.jsp");
        dispatcher.forward(request, response);
    }
}