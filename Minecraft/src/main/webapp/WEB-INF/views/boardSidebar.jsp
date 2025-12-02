<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 1. 세션에서 로그인 유저 정보 가져오기
    String sessionName = (String) session.getAttribute("userName");
    String sessionRole = (String) session.getAttribute("userRole");
    
    // 2. 화면 표시용 텍스트 변수 준비
    String displayInfo = "로그인이 필요합니다"; // 기본값
    
    // 3. 로그인 상태일 경우 권한 한글 변환 및 포맷팅 (권한 : 이름)
    if (sessionName != null) {
        String roleKo = "유저"; // 기본적으로 '유저'
        
        if ("ADMIN".equals(sessionRole)) {
            roleKo = "관리자";
        }
        
        displayInfo = roleKo + " : " + sessionName;
    }
%>

<aside class="board-sidebar">
    <div class="cafe-info-box">
        <div style="font-weight:bold; margin-bottom:5px;">CraftConnect</div>
        
        <%-- [수정] 매니저: 관리자 (고정값) -> 권한 : 이름 (동적값) --%>
        <div style="font-size:12px; color:#888; margin-bottom: 10px;">
            <%= displayInfo %>
        </div>
        
        <%-- 글쓰기 버튼 --%>
        <%-- (선택 사항: 비로그인 시 글쓰기 버튼을 숨기거나 로그인 페이지로 유도할 수도 있음) --%>
        <a href="#" class="btn-write link-write">게시판 작성</a>
    </div>

    <div class="menu-header">전체 메뉴</div>
    <ul class="sidebar-menu">
        <%-- 
            data-link="list": 목록 페이지 로드용
            data-category="...": 서블릿/DAO로 보낼 카테고리 코드 (BoardDAO와 일치해야 함)
        --%>
        <li class="active"><a href="#" data-link="list" data-category="ALL">전체글보기</a></li>
        <li><a href="#" data-link="list" data-category="NOTICE">공지사항</a></li>
        <li><a href="#" data-link="list" data-category="GREETING">가입 인사</a></li>
        
        <div class="menu-header">커뮤니티</div>
        <li><a href="#" data-link="list" data-category="FREE">자유 게시판</a></li>
        <li><a href="#" data-link="list" data-category="PROMOTION">서버 홍보</a></li>
        <li><a href="#" data-link="list" data-category="QNA">질문/답변</a></li>
        <li><a href="#" data-link="list" data-category="SCRIPT">스크립트</a></li>
    </ul>
</aside>