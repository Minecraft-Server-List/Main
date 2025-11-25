<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%
    // ------------------- [MOCK DATA 생성] -------------------
    // 실제 DB 연동 전, 화면 확인을 위한 가짜 데이터 생성 로직입니다.
    List<Map<String, String>> boardList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    String today = sdf.format(new Date());

    for (int i = 1; i <= 15; i++) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(100 - i)); // 글 번호 역순
        map.put("cat", i % 3 == 0 ? "[공지]" : "[자유]");
        map.put("title", "네이버 카페 스타일 게시판 디자인 테스트 게시글입니다. " + i);
        map.put("writer", "유저" + i);
        map.put("date", today);
        map.put("views", String.valueOf(i * 15));
        map.put("likes", String.valueOf(i * 2));
        boardList.add(map);
    }
    // -------------------------------------------------------
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>커뮤니티 게시판 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    
    <style>
        /* 1. 레이아웃: 좌측 사이드바 + 우측 컨텐츠 */
        .board-layout {
            display: flex;
            gap: 30px;
            padding: 40px 0;
            min-height: 800px;
        }

        /* 2. 좌측 사이드바 (메뉴) */
        .board-sidebar {
            width: 200px;
            flex-shrink: 0;
        }

        /* 카페 정보 박스 */
        .cafe-info-box {
            border: 1px solid #e5e5e5;
            padding: 20px 15px;
            margin-bottom: 20px;
            text-align: center;
            background: #fff;
        }
        .btn-write {
            display: block;
            width: 100%;
            background-color: #28a745; /* 기존 테마의 녹색 */
            color: #fff;
            padding: 12px 0;
            border-radius: 20px; /* 둥근 버튼 */
            text-align: center;
            font-weight: 700;
            font-size: 14px;
            margin-top: 10px;
            transition: background-color 0.2s;
        }
        .btn-write:hover { background-color: #218838; }

        /* 메뉴 리스트 */
        .sidebar-menu {
            list-style: none;
            border-top: 1px solid #eee;
        }
        .sidebar-menu li a {
            display: block;
            padding: 12px 10px;
            color: #333;
            font-size: 14px;
            border-bottom: 1px solid #eee;
            transition: all 0.2s;
        }
        .sidebar-menu li a:hover {
            background-color: #f9f9f9;
            color: #28a745;
            font-weight: 600;
        }
        .sidebar-menu li.active a {
            color: #28a745;
            font-weight: 700;
            border-left: 3px solid #28a745; /* 활성 탭 표시 */
            padding-left: 7px;
        }
        .menu-header {
            font-size: 13px;
            color: #888;
            margin-top: 20px;
            margin-bottom: 10px;
            padding-left: 5px;
        }

        /* 3. 우측 메인 컨텐츠 */
        .board-main {
            flex-grow: 1;
        }

        .board-header {
            display: flex;
            justify-content: space-between;
            align-items: end;
            margin-bottom: 15px;
            border-bottom: 2px solid #333; /* 헤더 하단 굵은 줄 */
            padding-bottom: 10px;
        }
        .board-title {
            font-size: 24px;
            font-weight: 700;
            color: #222;
        }
        .board-sort {
            font-size: 13px;
            color: #666;
        }
        .board-sort a { margin-left: 10px; }
        .board-sort a.active { font-weight: bold; color: #333; }

        /* 4. 게시판 테이블 스타일 */
        .board-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }
        .board-table th {
            padding: 15px 0;
            border-bottom: 1px solid #e5e5e5;
            color: #333;
            font-weight: 600;
            background-color: #f9f9f9;
        }
        .board-table td {
            padding: 15px 10px;
            border-bottom: 1px solid #e5e5e5;
            color: #555;
            text-align: center;
        }
        .board-table td.title-cell {
            text-align: left;
            padding-left: 20px;
        }
        .board-table td.title-cell a:hover {
            text-decoration: underline;
            color: #28a745;
        }
        .board-table tr:hover {
            background-color: #fcfcfc;
        }
        
        /* 공지사항 강조 */
        .notice-badge {
            font-weight: bold;
            color: #ef4646;
            margin-right: 5px;
        }

        /* 5. 페이지네이션 (하단 넘버 리스트) */
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 30px;
            gap: 5px;
        }
        .pagination a {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 32px;
            height: 32px;
            border: 1px solid #ddd;
            color: #666;
            font-size: 14px;
            background: #fff;
            transition: all 0.2s;
        }
        .pagination a.active {
            background-color: #28a745;
            color: #fff;
            border-color: #28a745;
            font-weight: bold;
        }
        .pagination a:hover:not(.active) {
            background-color: #f5f5f5;
        }

        /* 6. 검색창 (하단) */
        .board-search {
            margin-top: 20px;
            text-align: center;
        }
        .board-search select, .board-search input {
            padding: 8px;
            border: 1px solid #ccc;
            font-size: 13px;
        }
        .board-search button {
            padding: 8px 15px;
            background: #333;
            color: #fff;
            border: none;
            font-size: 13px;
            cursor: pointer;
        }
    </style>
</head>
<body>

    <%-- 헤더 include --%>
    <jsp:include page="/WEB-INF/views/header.jsp" />

    <main class="container">
        <div class="board-layout">
            
            <%-- [좌측] 사이드바 메뉴 영역 --%>
            <aside class="board-sidebar">
                <div class="cafe-info-box">
                    <div style="font-weight:bold; margin-bottom:5px;">CraftConnect</div>
                    <div style="font-size:12px; color:#888;">매니저: 관리자</div>
                    <a href="#" class="btn-write">카페 글쓰기</a>
                </div>

                <div class="menu-header">전체 메뉴</div>
                <ul class="sidebar-menu">
                    <li class="active"><a href="#">전체글보기</a></li>
                    <li><a href="#">필독 공지사항</a></li>
                    <li><a href="#">가입 인사</a></li>
                    
                    <div class="menu-header">커뮤니티</div>
                    <li><a href="#">자유 게시판</a></li>
                    <li><a href="#">서버 홍보</a></li>
                    <li><a href="#">질문/답변</a></li>
                    <li><a href="#">신고 센터</a></li>
                </ul>
            </aside>

            <%-- [우측] 게시판 메인 컨텐츠 영역 --%>
            <section class="board-main">
                
                <div class="board-header">
                    <div class="board-title">전체글보기</div>
                    <div class="board-sort">
                        <a href="#" class="active">최신순</a> | 
                        <a href="#">조회순</a> | 
                        <a href="#">좋아요순</a>
                    </div>
                </div>

                <table class="board-table">
                    <colgroup>
                        <col style="width: 8%;">  <col style="width: 50%;"> <col style="width: 15%;"> <col style="width: 12%;"> <col style="width: 8%;">  <col style="width: 7%;">  </colgroup>
                    <thead>
                        <tr>
                            <th>NO</th>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>작성일</th>
                            <th>조회</th>
                            <th>좋아요</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- MOCK DATA 출력 루프 --%>
                        <% for(Map<String, String> post : boardList) { %>
                        <tr>
                            <td><%= post.get("id") %></td>
                            <td class="title-cell">
                                <% if(post.get("cat").equals("[공지]")) { %>
                                    <span class="notice-badge">[공지]</span>
                                <% } %>
                                <a href="boardDetail.do?id=<%= post.get("id") %>">
                                    <%= post.get("title") %>
                                </a>
                                <span style="color:#ff4e50; font-weight:bold; font-size:12px;">[5]</span>
                            </td>
                            <td>
                                <a href="#" style="color:#333;"><%= post.get("writer") %></a>
                            </td>
                            <td style="font-size:13px;"><%= post.get("date") %></td>
                            <td style="font-size:13px; color:#888;"><%= post.get("views") %></td>
                            <td style="font-size:13px; color:#888;"><%= post.get("likes") %></td>
                        </tr>
                        <% } %>
                        
                        <%-- 데이터가 없을 경우 --%>
                        <% if(boardList.isEmpty()) { %>
                            <tr><td colspan="6" style="padding: 50px 0;">작성된 게시글이 없습니다.</td></tr>
                        <% } %>
                    </tbody>
                </table>

                <div style="text-align: right; margin-top: 15px;">
                    <a href="#" style="font-size:13px; font-weight:bold; color:#333;">글쓰기</a>
                </div>

                <div class="pagination">
                    <a href="#">&lt;</a> <a href="#" class="active">1</a>
                    <a href="#">2</a>
                    <a href="#">3</a>
                    <a href="#">4</a>
                    <a href="#">5</a>
                    <a href="#">&gt;</a> </div>

                <div class="board-search">
                    <form action="search.do" method="get">
                        <select name="searchType">
                            <option value="title">제목만</option>
                            <option value="writer">작성자</option>
                            <option value="content">내용</option>
                        </select>
                        <input type="text" name="keyword" placeholder="검색어를 입력하세요">
                        <button type="submit">검색</button>
                    </form>
                </div>

            </section>
        </div>
    </main>

    <%-- 푸터 include --%>
    <jsp:include page="/WEB-INF/views/footer.jsp" />

</body>
</html>