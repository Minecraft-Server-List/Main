package com.example.minecraft.controller;

import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.service.ServerService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

public class ServerStatusApiController extends HttpServlet {

    private final ServerService serverService = new ServerService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Service에서 서버 목록 조회 (캐싱 로직이 포함되어 속도가 빠름)
        // 🚨 Service는 이미 ServerStatusDTO를 포함하여 모든 정보를 가져옵니다.
        ArrayList<ServerDTO> serverList = serverService.getServerListService();

        // 2. JSON 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 3. ServerDTO 목록을 JSON 문자열로 변환하여 출력
            String jsonOutput = gson.toJson(serverList);
            response.getWriter().write(jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 빈 JSON 배열 반환
            response.getWriter().write("[]");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}