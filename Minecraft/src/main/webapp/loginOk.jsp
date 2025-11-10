<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<title>로그인 성공</title>
<head>
</head>
<body>

<a href="index.jsp">[홈으로 돌아가기]</a>

<hr> <h1>로그인 성공</h1>

<%
    // LoginServlit에서 저장한 "userName"과 "userEmail"을 가져옵니다.
    String name = (String) session.getAttribute("userName");
    String email = (String) session.getAttribute("userEmail");
%>

<p><strong><%= name %></strong>님, 환영합니다.</p>
<p>(이메일: <%= email %>)</p>

</body>
</html>