package com.example.minecraft.Util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 1. URL 매핑: 주소가 ".page"로 끝나는 모든 요청을 이 서블릿이 낚아챕니다.
@WebServlet("*.page")
public class PageController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	// GET이든 POST든 이 메서드에서 처리합니다.
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 2. 사용자가 요청한 주소(URI) 분석
		String uri = request.getRequestURI(); 
		// 예: /Minecraft/login.page
		
		String conPath = request.getContextPath(); 
		// 예: /Minecraft
		
		String command = uri.substring(conPath.length()); 
		// 결과: /login.page
		
		String viewPage = null;

		// 3. 요청에 따라 보여줄 JSP 파일(목적지) 설정
		switch (command) {
			case "/login.page":
				// 로그인 화면 보여주기
				viewPage = "/WEB-INF/views/login.jsp";
				break;
				
			case "/register.page":
				// 회원가입 화면 보여주기
				viewPage = "/WEB-INF/views/registerForm.jsp";
				break;
				
			case "/index.page":
				// 회원가입 화면 보여주기
				viewPage = "/index.jsp";
				break;

            case "/serverAdd.page":
                viewPage = "/WEB-INF/views/server/form.jsp";
                break;
			// 추후 다른 단순 페이지가 생기면 여기에 case만 추가하면 됩니다.
			// 예: case "/intro.page": viewPage = "/WEB-INF/views/intro.jsp"; break;
		}

		// 4. 목적지로 포워딩 (화면 이동)
		if (viewPage != null) {
			request.getRequestDispatcher(viewPage).forward(request, response);
		} else {
			// 매핑된 페이지가 없을 경우 메인으로 보냄
			response.sendRedirect(conPath + "/index.jsp");
		}
	}
}