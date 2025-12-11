package com.example.minecraft.controller;

import com.example.minecraft.service.ServerService;
import com.example.minecraft.dto.ServerDTO;
import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/serverList")
public class ServerListController extends HttpServlet {

    private final ServerService serverService = new ServerService();
    private static final String JSP_PATH = "/WEB-INF/views/server/server-list.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 💡 1. 검색어 파라미터 획득 및 처리
        String query = request.getParameter("query");

        ArrayList<ServerDTO> serverList;

        try {
            if (query != null && !query.trim().isEmpty()) {
                // 🚨 검색어가 있을 경우: 검색 서비스 호출
                serverList = serverService.searchServersService(query);

                // 검색어를 JSP로 다시 보내 검색창에 유지되도록 설정
                request.setAttribute("searchQuery", query);

                // 디버깅/로그 용 (선택 사항)
                System.out.println("DEBUG: 서버 검색 실행, 쿼리: " + query);

            } else {
                // 🚨 검색어가 없을 경우: 전체 목록 서비스 호출
                serverList = serverService.getServerListService();

                // 디버깅/로그 용 (선택 사항)
                System.out.println("DEBUG: 전체 서버 목록 조회 실행.");
            }

            // 2. 데이터를 Request 객체에 담아 JSP로 전달
            request.setAttribute("serverList", serverList);

            // 3. JSP 파일로 포워딩
            RequestDispatcher dispatcher = request.getRequestDispatcher(JSP_PATH);
            dispatcher.forward(request, response);

        } catch (Exception e) {
            // 모든 예외를 여기서 처리
            e.printStackTrace();
            // HTTP 500 오류 응답 반환
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 목록 처리 중 오류 발생: " + e.getMessage());
        }
    }
}