<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/form-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

    <%@ include file="../header.jsp" %>  <main class="form-page-main">
        <div class="container">
            
            <%@ include file="mypageHeader.jsp" %>
            
            <%-- 🚨 AJAX 콘텐츠가 삽입될 영역 🚨 --%>
            <div class="mypage-content-area">
                <p style="padding: 30px; text-align: center;">마이페이지를 로드하는 중...</p>
            </div>
            
        </div>
    </main>

    <%@ include file="../footer.jsp" %>  </body>
</html>