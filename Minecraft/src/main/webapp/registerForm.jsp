<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입 - CraftConnect</title>
    
    <%-- [추가] 범용 폼 CSS 링크 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/form-style.css">

</head>
<body>

<%-- [추가] 공통 헤더 --%>
<%@ include file = "header.jsp" %>

<%-- [추가] 폼 페이지용 <main> 태그 --%>
<main class="form-page-main">
    <div class="container">
        
        <%-- [추가] 범용 폼 컨테이너 --%>
        <div class="user-form-container">
            <h2>회원가입</h2>
            <p style="text-align: center; margin-bottom: 20px;">사용할 이름, 이메일, 비밀번호를 입력하세요.</p>
        
            <%-- 
              [수정]
              기존 <table> 구조 대신 <div> 구조로 변경하여 CSS를 적용합니다.
            --%>
            <form action="register.do" method="post">
                <div class="user-form-group">
                    <label for="name">이름</label>
                    <input type="text" id="name" name="name" required>
                </div>
                <div class="user-form-group">
                    <label for="email">이메일 (ID)</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="user-form-group">
                    <label for="password">비밀번호</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <div class="user-form-group">
                    <input type="submit" value="가입하기">
                    <input type="reset" value="다시작성">
                </div>
            </form>
        </div>
    </div>
</main>

<%-- [추가] 공통 푸터 --%>
<footer class="main-footer">
    <div class="container">
        <nav class="footer-nav">
            <a href="#">About</a>
            <a href="#">Contact</a>
            <a href="#">Terms of Service</a>
            <a href="#">Privacy Policy</a>
        </nav>
        <div class="footer-socials">
            <a href="#"><i class='bx bxl-twitter'></i></a>
            <a href="#"><i class='bx bxl-facebook-square'></i></a>
            <a href="#"><i class='bx bxl-instagram'></i></a>
        </div>
        <p class="copyright">@2023 CraftConnect. All rights reserved.</p>
    </div>
</footer>

</body>
</html>