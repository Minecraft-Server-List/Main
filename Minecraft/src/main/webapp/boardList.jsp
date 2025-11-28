<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%
    // Mock Data 생성
    List<Map<String, String>> boardList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    String today = sdf.format(new Date());

    for (int i = 1; i <= 10; i++) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(100 - i));
        map.put("cat", i % 3 == 0 ? "[공지]" : "[자유]");
        map.put("title", "SPA 방식 게시판 테스트 글입니다. " + i);
        map.put("writer", "유저" + i);
        map.put("date", today);
        map.put("views", String.valueOf(i * 15));
        map.put("likes", String.valueOf(i * 2));
        boardList.add(map);
    }
%>

<div class="page-section">
    <div class="board-header">
        <div class="board-title">전체글보기</div>
        <div class="board-sort">
            <a href="#" class="active">최신순</a> | <a href="#">조회순</a> | <a href="#">좋아요순</a>
        </div>
    </div>

    <table class="board-table">
        <colgroup>
            <col style="width: 8%;"> <col style="width: 50%;"> <col style="width: 15%;"> <col style="width: 12%;"> <col style="width: 8%;"> <col style="width: 7%;">
        </colgroup>
        <thead>
            <tr>
                <th>NO</th><th>제목</th><th>작성자</th><th>작성일</th><th>조회</th><th>좋아요</th>
            </tr>
        </thead>
        <tbody>
            <% for(Map<String, String> post : boardList) { %>
            <tr>
                <td><%= post.get("id") %></td>
                <td class="title-cell">
                    <% if(post.get("cat").equals("[공지]")) { %>
                        <span class="notice-badge">[공지]</span>
                    <% } %>
                    <a href="#" class="link-detail" data-id="<%= post.get("id") %>">
                        <%= post.get("title") %>
                    </a>
                    <span style="color:#ff4e50; font-weight:bold; font-size:12px;">[5]</span>
                </td>
                <td><%= post.get("writer") %></td>
                <td><%= post.get("date") %></td>
                <td><%= post.get("views") %></td>
                <td><%= post.get("likes") %></td>
            </tr>
            <% } %>
        </tbody>
    </table>

    <div style="text-align: right; margin-top: 15px;">
        <button class="btn-write link-write" style="width:auto; padding: 8px 20px; display:inline-block;">글쓰기</button>
    </div>

    <div class="pagination">
        <a href="#">&lt;</a> <a href="#" class="active">1</a> <a href="#">2</a> <a href="#">&gt;</a>
    </div>

    <div class="board-search">
        <select><option>제목만</option></select>
        <input type="text" placeholder="검색어를 입력하세요">
        <button>검색</button>
    </div>
</div>