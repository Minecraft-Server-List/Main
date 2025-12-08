package com.example.minecraft.controller;

import com.example.minecraft.dto.ServerDTO;
import com.example.minecraft.service.ServerService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/server.do")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5
)
public class ServerController extends HttpServlet {

    private ServerService serverService;

    // 초기화
    // 서블릿 객체가 최초 한 번만 실행하여 수백, 수천 개 요청이 들어와도 하나의 서비스 객체만을 재사용
    @Override
    public void init() throws ServletException {
        this.serverService = new ServerService();
    }

    // 1. 서버 생성
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // JSP 에서 POST 요청이 들어왔을 때, 어떤 POST 요청인지 확인하기 위한 구문
        String action = request.getParameter("action");

        Long id = 0L;

        if (request.getParameter("id") != null &&  !request.getParameter("id").equals("")) {
            id = Long.parseLong(request.getParameter("id"));
        }

        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String version =  request.getParameter("version");
        String domain = request.getParameter("domain");

        Part serverImagePart = request.getPart("serverImage");

        status = "ACTIVE";
        ServerDTO serverDTO = new ServerDTO(name, status, version, domain);
        boolean success = false;

        jakarta.servlet.ServletContext context = request.getServletContext();

        if (action.equals("create")) {
            try {
                // 🚨 수정: Service 호출 시 context 객체 전달
                success = serverService.createServerService(serverDTO, serverImagePart, context);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        else if (action.equals("update")) {
            success = serverService.updateServerService(serverDTO);
        }

        else if (action.equals("delete")) {
            success = serverService.deleteServerService(id);
        }

        if (success) {
            response.sendRedirect(request.getContextPath() + "/server.do?action=" + action);
        } else {
            System.out.println("서버 " + action + " 실패");
        }

    }

    // 2. 서버 조회
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        String viewPage = "";

        // 2-1. 서버 목록 조회
        if (action.equals("list") || action == null) {
            ArrayList<ServerDTO> serverList = serverService.getServerListService();
            request.setAttribute("serverList", serverList);
            viewPage = "/WEB-INF/views/server/server_list.jsp";
        }

        // 2-2. 서버 단일 조회
        else if (action.equals("view")) {
            long id = Long.parseLong(request.getParameter("id"));
            ServerDTO server = serverService.getServerByIdService(id);
            request.setAttribute("server", server);
            viewPage = "/WEB-INF/views/server/server-detail.jsp";
        }

        else {
            viewPage = "/WEB-INF/views/error.jsp";
        }

        // View(JSP)로 포워딩
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
        dispatcher.forward(request, response);

    }
}
