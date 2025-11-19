<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/form-style.css">
</head>
<body>

    <%@ include file="header.jsp" %>

    <main class="form-page-main">
        <div class="container">
            <div class="user-form-container">
                <h2>로그인</h2>
                
                <%-- [유지] 로그인 처리(DB 확인)는 서블릿(.do)이 담당 --%>
                <form action="login.do" method="post">
                    <div class="user-form-group">
                        <label for="email_id">이메일 (ID)</label>
                        <input type="text" id="email_id" name="id" required>
                    </div>
                    <div class="user-form-group">
                        <label for="password_id">비밀번호</label>
                        <input type="password" id="password_id" name="pw" required>
                    </div>
                    <div class="user-form-group">
                        <input type="submit" value="로그인">
                    </div>
                </form>
            </div>
        </div>
    </main>

    <%@ include file="footer.jsp" %>

</body>
</html>