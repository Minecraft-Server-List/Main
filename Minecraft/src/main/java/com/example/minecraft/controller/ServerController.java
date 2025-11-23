package com.example.minecraft.controller;

import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.service.ServerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/server")
public class ServerController {

    private ServerService serverService;

    // 초기화
    // 서블릿 객체가 최초 한 번만 실행하여 수백, 수천 개 요청이 들어와도 하나의 서비스 객체만을 재사용
    public void init() throws ServletException {
        this.serverService = new ServerService();
    }

    // 1. 서버 생성
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // JSP 에서 POST 요청이 들어왔을 때, 어떤 POST 요청인지 확인하기 위한 구문
        String action = request.getParameter("action");

        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String version =  request.getParameter("version");
        String domain = request.getParameter("domain");

        ServerDTO serverDTO = new ServerDTO(name, status, version, domain);
        boolean success = false;

        if (action.equals("create")) { // 어떤 POST 요청인지 확인
            success = serverService.createServerService(serverDTO);
        }

        if (success) {
            response.sendRedirect(request.getContextPath() + "/server?action=" + action);
        } else {
            System.out.println("서버 생성 실패");
        }

    }
}
