package com.example.minecraft.controller;

import com.example.minecraft.service.ServerService;
import com.example.minecraft.dto.ServerDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        // 1. 파라미터 획득
        String query = request.getParameter("query");
        String category = request.getParameter("category");

        ArrayList<ServerDTO> serverList;

        try {
            // 🌟 수정: 통합 서비스 호출 (검색어와 카테고리 모두 전달)
            serverList = serverService.searchAndFilterServersService(query, category);

            // 검색어는 JSP에서 사용하도록 설정
            if (query != null && !query.trim().isEmpty()) {
                request.setAttribute("searchQuery", query);
                System.out.println("DEBUG: 통합 검색 실행, 쿼리: [" + query + "], 카테고리: [" + category + "]");
            } else if (category != null && !category.trim().isEmpty()) {
                System.out.println("DEBUG: 카테고리 필터링 실행: [" + category + "]");
            } else {
                System.out.println("DEBUG: 전체 서버 목록 조회 실행.");
            }

            // 2. 서버 목록 데이터를 Request 객체에 담아 JSP로 전달
            request.setAttribute("serverList", serverList);

            // 3. 카테고리 목록을 조회하고 JSP로 전달 (사이드바 출력용)
            List<String> categoryList = serverService.getAllCategoriesService();
            request.setAttribute("categoryList", categoryList);

            // 4. JSP 파일로 포워딩
            RequestDispatcher dispatcher = request.getRequestDispatcher(JSP_PATH);
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 목록 처리 중 오류 발생: " + e.getMessage());
        }
    }
}