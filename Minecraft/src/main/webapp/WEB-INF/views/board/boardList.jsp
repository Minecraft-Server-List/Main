<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.example.minecraft.dto.BoardDTO" %>
<%
    List<BoardDTO> boardList = (List<BoardDTO>) request.getAttribute("boardList");
    int currentPage = (Integer) request.getAttribute("currentPage");
    String currentCategory = (String) request.getAttribute("currentCategory");
    String categoryName = (String) request.getAttribute("categoryName");
%>

<div class="page-section">
    <div class="board-header">
        <div class="board-title"><%= categoryName %></div>
        <div class="board-sort">
            <a href="#" class="active">최신순</a>
        </div>
    </div>

    <table class="board-table">
        <colgroup>
            <col style="width: 8%;"> <col style="width: 50%;"> <col style="width: 15%;"> <col style="width: 15%;"> <col style="width: 6%;"> <col style="width: 6%;">
        </colgroup>
        <thead>
            <tr>
                <th>NO</th><th>제목</th><th>작성자</th><th>작성일</th><th>조회</th><th>좋아요</th>
            </tr>
        </thead>
        <tbody>
            <% if(boardList != null && !boardList.isEmpty()) { 
                 for(BoardDTO post : boardList) { 
            %>
            <tr>
                <td><%= post.getBaseBoardId() %></td>
                <td class="title-cell">
                    <% if("ALL".equals(currentCategory)) { %>
                        <span style="font-size:11px; color:#888;">[<%= post.getCategory() %>]</span>
                    <% } %>
                    
                    <%-- 
                        [핵심 수정] 
                        1. href="#" : 페이지 이동 차단
                        2. onclick="router.goPost(...)" : 메인 페이지에 있는 자바스크립트 함수 실행
                        3. return false : 링크 기본 동작(상단 이동 등) 차단
                    --%>
                    <a href="#" onclick="router.goPost(<%= post.getBaseBoardId() %>); return false;" style="text-decoration:none; color:#333; font-weight:bold; cursor:pointer;">
                        <%= post.getTitle() %>
                    </a>
                    
                    <% if(post.getLikeCount() >= 5) { %>
                        <span style="color:#ff4e50; font-weight:bold; font-size:11px;">HOT</span>
                    <% } %>
                </td>
                <td><%= post.getWriterName() %></td>
                <td><%= post.getCreatedAt().toLocalDate() %></td> 
                <td><%= post.getViewCount() %></td>
                <td><%= post.getLikeCount() %></td>
            </tr>
            <%   } 
               } else { %>
            <tr>
                <td colspan="6" style="text-align:center; padding:50px; color:#999;">게시글이 없습니다.</td>
            </tr>
            <% } %>
        </tbody>
    </table>

    <div style="text-align: right; margin-top: 15px;">
        <%-- [글쓰기 버튼] ID 없이 goPost()를 호출하면 작성 모드가 됩니다 --%>
        <button class="btn-write" onclick="router.goPost();" style="width:auto; padding: 8px 20px; display:inline-block;">글쓰기</button>
    </div>

    <div class="pagination">
        <%-- 페이징도 router를 타야 화면 안에서 바뀝니다 --%>
        <a href="#" onclick="router.goList(<%= currentPage - 1 < 1 ? 1 : currentPage - 1 %>); return false;">&lt;</a>
        <a href="#" class="active"><%= currentPage %></a>
        <a href="#" onclick="router.goList(<%= currentPage + 1 %>); return false;">&gt;</a>
    </div>
</div>