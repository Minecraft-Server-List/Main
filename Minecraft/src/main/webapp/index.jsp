<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>홈</title>
</head>
<body>

<%@ include file = "header.jsp" %>
<%
    // 1. 세션에서 로그인 정보 (이름, 역할)를 가져옵니다.
    String userName = (String) session.getAttribute("userName");
    String userRole = (String) session.getAttribute("userRole"); // (LoginServlit에서 저장)

    // 2. userName이 null이 아니면 (로그인 상태)
    if(userName != null){
%>
    <p><strong><%= userName %></strong>님 (등급: <%= userRole %>) 환영합니다.</p>
    <a href="logout.do">[로그아웃]</a>
    <hr>
    
<%
    
    // 3. userName이 null이면 (로그아웃 상태)
    } else {
%>
    <h3>로그인</h3>
    <form action="login.do" method="post"> 
        이메일 : <input type="text" id="a" name="id"><br> 
        암호 : <input type="password" id="b" name="pw"><br>
        <input type="submit" value="로그인">
        <input type="reset" value="초기화">
    </form>
    
    <%
    }
%>

<hr>
<h1>ㅎㅇ</h1> </body>
</html>