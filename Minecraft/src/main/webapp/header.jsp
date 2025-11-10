<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // 1. 세션에서 'userName'과 'userRole', 'userEmail'을 가져옵니다.
    // (변수명이 겹치지 않도록 _header 접미사를 붙였습니다)
    String userName_header = (String) session.getAttribute("userName");
    String userRole_header = (String) session.getAttribute("userRole");
    String userEmail_header = (String) session.getAttribute("userEmail");
%>
<h3>
    메뉴 : 
    <a href="index.jsp">홈</a> | 
    <a href="intro.jsp">게시판</a> 
<%
    // 2. 관리자(ADMIN)일 경우에만 '회원 목록' 링크를 보여줍니다.
    if ("ADMIN".equals(userRole_header)) {
        out.print("| <a href='userList.do'>회원 목록(관리자)</a>");
    }

    // 3. 로그인 상태에 따라 '마이페이지' 또는 '회원가입' 링크를 분기합니다.
    if (userName_header == null) { // 로그아웃 상태
%>
        | <a href='registerForm.jsp'>회원가입</a>
<%
    } else { // 로그인 상태
%>
        | <a href='searchUser.do?email=<%= userEmail_header %>'>마이페이지</a>
<%
    }
%>
</h3>
