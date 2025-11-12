<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 - CraftConnect</title>
    
    <%-- 
      [수정]
      <style> 태그 대신 <link> 태그로 새 CSS 파일을 불러옵니다.
    --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/form-style.css">

</head>
<body>

    <%@ include file="header.jsp" %>

    <%-- 
      [수정]
      <main>에 CSS가 적용되도록 class="form-page-main" 추가
    --%>
    <main class="form-page-main">
        <div class="container">
            
            <%-- [수정] class="login-form-container" -> "user-form-container" --%>
            <div class="user-form-container">
                <h2>로그인</h2>
                
                <form action="login.do" method="post">
                    <%-- [수정] class="login-form-group" -> "user-form-group" --%>
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