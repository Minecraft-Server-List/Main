<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<aside class="board-sidebar">
    <div class="cafe-info-box">
        <div style="font-weight:bold; margin-bottom:5px;">CraftConnect</div>
        <div style="font-size:12px; color:#888;">매니저: 관리자</div>
        <%-- 글쓰기 버튼 --%>
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