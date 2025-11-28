package com.example.minecraft.controller;

import com.example.minecraft.service.ServerService;
import com.example.minecraft.dto.ServerDTO;
import java.io.IOException;
import java.util.ArrayList;
// 🚨 Jakarta EE 변경 사항: jakarta.servlet.* 사용
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/serverList")
public class ServerListController extends HttpServlet {

    private final ServerService serverService = new ServerService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Service 호출
            ArrayList<ServerDTO> serverList = serverService.getServerListService();

            // 2. 데이터를 Request 객체에 담아 JSP로 전달
            request.setAttribute("serverList", serverList);

            // 3. JSP 파일로 포워딩 (경로: /WEB-INF/views/server/server-list.jsp)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/server/server-list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 목록 처리 중 오류 발생");
        }
    }
}