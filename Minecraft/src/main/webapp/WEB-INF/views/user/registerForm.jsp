<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/form-style.css">
</head>
<body>

<%@ include file = "../header.jsp" %>

<main class="form-page-main">
    <div class="container">
        <div class="user-form-container">
            <h2>회원가입</h2>
            <p style="text-align: center; margin-bottom: 20px;">사용할 이름, 이메일, 비밀번호를 입력하세요.</p>
        
            <%-- [변경] 통합된 UserServlet 경로로 수정 --%>
            <form action="${pageContext.request.contextPath}/user/register" method="post">
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

<%@ include file="../footer.jsp" %>

</body>
</html>