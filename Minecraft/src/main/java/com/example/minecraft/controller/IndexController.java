package com.example.minecraft.controller;

import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.service.ServerService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

// @WebServlet("/index")
public class IndexController extends HttpServlet {

    private ServerService serverService;

    @Override
    public void init() throws ServletException {
        this.serverService = new ServerService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. 서버 목록 전체를 최신순으로 가져옵니다.
            ArrayList<ServerDTO> serverList = serverService.getServerListService();
            System.out.println(">>> IndexController: DAO에서 가져온 서버 개수: " + serverList.size());

            // 2. JSP에서 최근 3개만 사용할 수 있도록 리스트를 전달합니다.
            //    (JSP에서 <c:forEach end="2">로 3개만 사용하도록 처리했습니다.)
            request.setAttribute("serverList", serverList);

            // 3. index.jsp로 포워딩
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "메인 페이지 로드 중 오류 발생");
        }
    }
}
