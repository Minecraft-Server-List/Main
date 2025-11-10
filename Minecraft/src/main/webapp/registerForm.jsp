<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
</head>
<body>

<a href="index.jsp">[홈으로 돌아가기]</a>
<hr>

<h2>회원가입</h2>
<p>사용할 이름, 이메일, 비밀번호를 입력하세요.</p>

<form action="register.do" method="post">
    <table border="1">
        <tr>
            <td>이름</td>
            <td><input type="text" name="name" required></td>
        </tr>
        <tr>
            <td>이메일 (ID)</td>
            <td><input type="email" name="email" required></td>
        </tr>
        <tr>
            <td>비밀번호</td>
            <td><input type="password" name="password" required></td>
        </tr>
    </table>
    <br>
    <input type="submit" value="가입하기">
    <input type="reset" value="다시작성">
</form>

</body>
</html>