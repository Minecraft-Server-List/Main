<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.example.minecraft.dto.BoardDTO, com.example.minecraft.dto.ComentDTO" %>

<%
    String currentType = (String) request.getAttribute("currentType");
    List<?> dataList = (List<?>) request.getAttribute("dataList");
    String title = "posts".equals(currentType) ? "내가 쓴 게시글" : "내가 쓴 댓글";
    
    // [중요] 게시판 메인 페이지 URL (헤더의 커뮤니티 링크와 동일하게 설정)
    String boardMainUrl = request.getContextPath() + "/board.page"; 
%>

<div class="page-section">
    <div class="board-header">
        <div class="board-title" style="font-size:24px;"><%= title %></div>
        <div class="board-sort">
            <span style="font-weight:bold; color:#28a745;">Total: <%= dataList != null ? dataList.size() : 0 %>건</span>
        </div>
    </div>

    <table class="board-table">
        
        <%-- [CASE 1] 게시글 목록 --%>
        <% if ("posts".equals(currentType)) { %>
            <colgroup>
                <col style="width: 8%;"> <col style="width: 50%;"> <col style="width: 15%;"> <col style="width: 15%;"> <col style="width: 6%;"> <col style="width: 6%;">
            </colgroup>
            <thead>
                <tr>
                    <th>NO</th><th>제목</th><th>작성자</th><th>작성일</th><th>조회</th><th>좋아요</th>
                </tr>
            </thead>
            <tbody>
                <% if(dataList != null && !dataList.isEmpty()) { 
                     for(Object obj : dataList) {
                         BoardDTO p = (BoardDTO) obj;
                %>
                <tr>
                    <td><%= p.getBaseBoardId() %></td>
                    <td class="title-cell">
                        <span style="font-size:11px; color:#888;">[<%= p.getCategory() %>]</span>
                        
                        <%-- [이동] 게시판 메인으로 이동하며 viewId 파라미터 전달 --%>
                        <a href="<%= boardMainUrl %>?viewId=<%= p.getBaseBoardId() %>" 
                           style="cursor:pointer; font-weight:bold; color:#333;">
                            <%= p.getTitle() %>
                        </a>
                        
                        <% if(p.getLikeCount() >= 5) { %>
                            <span style="color:#ff4e50; font-weight:bold; font-size:11px;">HOT</span>
                        <% } %>
                    </td>
                    <td><%= p.getWriterName() %></td>
                    <td><%= p.getCreatedAt().toLocalDate() %></td> 
                    <td><%= p.getViewCount() %></td>
                    <td><%= p.getLikeCount() %></td>
                </tr>
                <% } } else { %>
                <tr><td colspan="6" style="text-align:center; padding:50px; color:#999;">작성한 게시글이 없습니다.</td></tr>
                <% } %>
            </tbody>

        <%-- [CASE 2] 댓글 목록 --%>
        <% } else { %>
            <colgroup>
                <col style="width: 50%;"> <col style="width: 30%;"> <col style="width: 15%;"> <col style="width: 5%;">
            </colgroup>
            <thead>
                <tr>
                    <th>댓글 내용</th><th>원문 제목</th><th>작성일</th><th>좋아요</th>
                </tr>
            </thead>
            <tbody>
                <% if(dataList != null && !dataList.isEmpty()) { 
                     for(Object obj : dataList) {
                         ComentDTO c = (ComentDTO) obj;
                %>
                <tr>
                    <td class="title-cell" style="padding-left:10px;">
                        <%= c.getContent() %>
                    </td>
                    <td style="text-align:left; color:#666; font-size:13px;">
                        <%-- [이동] 원문 제목 클릭 시 해당 게시글로 이동 --%>
                        <a href="<%= boardMainUrl %>?viewId=<%= c.getBaseBoardId() %>"
                           style="cursor:pointer; text-decoration:underline;">
                            원문: <%= c.getBoardTitle() %>
                        </a>
                    </td>
                    <td><%= c.getCreatedAt().toLocalDate() %></td>
                    <td><%= c.getLikeCount() %></td>
                </tr>
                <% } } else { %>
                <tr><td colspan="4" style="text-align:center; padding:50px; color:#999;">작성한 댓글이 없습니다.</td></tr>
                <% } %>
            </tbody>
        <% } %>
        
    </table>
</div>