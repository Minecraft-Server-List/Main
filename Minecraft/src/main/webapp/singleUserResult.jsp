<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, com.example.minecraft.User.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사용자 관리 결과 - CraftConnect</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/form-style.css">
    
</head>
<body>

<%@ include file = "header.jsp" %>

<%
    // --- 1. request 영역에서 모든 속성을 가져옵니다. ---
    String deletedEmail = (String) request.getAttribute("deletedEmail");
    UserDTO oldUser = (UserDTO) request.getAttribute("oldUserData");
    UserDTO newUser = (UserDTO) request.getAttribute("newUserData");
    UserDTO userToEdit = (UserDTO) request.getAttribute("userToEdit");
    String message = (String) request.getAttribute("message");

    // --- 2. session 영역에서 권한 속성을 가져옵니다. ---
    String userRole = (String) session.getAttribute("userRole");
%>

<main class="form-page-main">
    <div class="container">

<%
	// --- 3. 로직 분기 ---
	
    // Case 1: [삭제 성공]
	if (deletedEmail != null) {
%>
    <%-- [유지] 550px 표준 컨테이너 사용 --%>
    <div class="user-form-container result-message">
        <h2>계정 삭제 완료</h2>
        <hr style="margin-bottom: 24px;">
        <p><font color="red">'<%= deletedEmail %>'</font> 계정이 삭제되었습니다.</p>
<%
    if("ADMIN".equals(userRole)) {
%>
        <a href="userList.do">[회원 목록으로 가기]</a>
<%
    } 
%>
        <a href="index.jsp">[홈으로 가기]</a>
    </div>

<%
    // Case 2: [수정 성공]
	} else if (oldUser != null && newUser != null) {
%>
    <%-- [수정] 800px 전용 'wide-result-container' 사용 --%>
    <div class="wide-result-container">
        <h2>회원 정보 수정 완료</h2>
        <p style="text-align: center; margin-bottom: 20px;">사용자 정보가 성공적으로 수정되었습니다.</p>
    
        <table class="simple-table">
            <tr>
                <th>항목</th>
                <th>변경 전 (Previous)</th>
                <th>변경 후 (Current)</th>
            </tr>
            <tr>
                <td>이름</td>
                <td><%= oldUser.getName() %></td>
                <td><strong><%= newUser.getName() %></strong></td>
            </tr>
            <tr>
                <td>이메일</td>
                <td><%= oldUser.getEmail() %></td>
                <td><strong><%= newUser.getEmail() %></strong></td>
            </tr>
            <tr>
                <td>역할</td>
                <td><%= oldUser.getRole() %></td>
                <td><strong><%= newUser.getRole() %></strong></td>
            </tr>
            <tr>
                <td>비밀번호</td>
                <td>(비공개)</td>
                <% 
                    if (!newUser.getPassword().equals(oldUser.getPassword())) {
                %>
                    <td><font color="blue"><strong>(새 비밀번호로 변경됨)</strong></font></td>
                <% } else { %>
                    <td>(변경 없음)</td>
                <% } %>
            </tr>
        </table>
        
        <div class="user-form-buttons">
<%
    if("ADMIN".equals(userRole)) {
        // [ADMIN] 버튼 2개
%>
            <input type="button" value="회원 목록으로" class="btn btn-secondary" onclick="location.href='userList.do'">
            <input type="button" value="홈으로 가기" class="btn btn-primary" onclick="location.href='index.jsp'">
<%
    } else {
        // [USER] 버튼 1개 (중앙 정렬됨)
%>
            <input type="button" value="홈으로 가기" class="btn btn-primary" onclick="location.href='index.jsp'">
<%
    } 
%>
        </div>
    </div>
<%
// Case 3: [검색 성공] (수정 폼)
} else if (userToEdit != null) {
%>
<%-- [유지] 550px 표준 컨테이너 사용 --%>
<div class="user-form-container">
    <h2>회원 정보 수정</h2>
    <hr style="margin-bottom: 24px;">
    
    <form action="updateUser.do" method="post">
        
        <input type="hidden" name="userId" value="<%= userToEdit.getUserId() %>">
        
        <div class="user-form-group">
            <label>사용자 ID (수정불가)</label>
            <input type="text" value="<%= userToEdit.getUserId() %>" readonly>
        </div>
        
         <div class="user-form-group">
            <label>가입일시 (수정불가)</label>
            <input type="text" value="<%= userToEdit.getCreatedAt() %>" readonly>
        </div>
        
         <div class="user-form-group">
            <label for="name">이름</label>
            <input type="text" id="name" name="name" value="<%= userToEdit.getName() %>">
        </div>
        
        <div class="user-form-group">
            <label for="email">이메일</label>
            <input type="email" id="email" name="email" value="<%= userToEdit.getEmail() %>">
        </div>
        
        <div class="user-form-group">
            <label for="password">새 비밀번호 (변경할 경우에만 입력)</label>
            <input type="password" id="password" name="password" placeholder="변경할 경우에만 입력하세요">
        </div>

        <div class="user-form-group">
            <label for="role">역할 (ADMIN / USER)</label>
<%
if ("ADMIN".equals(userRole)) {
%>
                <select name="role" id="role">
                    <option value="ADMIN" <%= "ADMIN".equals(userToEdit.getRole()) ? "selected" : "" %>>ADMIN</option>
                    <option value="USER"  <%= "USER".equals(userToEdit.getRole()) ? "selected" : "" %>>USER</option>
                </select>
<%
} else {
%>
                <input type="text" value="<%= userToEdit.getRole() %>" readonly>
                <input type="hidden" name="role" value="<%= userToEdit.getRole() %>">
<%
} // if-else 종료
%>
        </div>
    
        <div class="user-form-group">
            <input type="submit" value="정보 수정하기">
<%
if("ADMIN".equals(userRole)) {
%>
            <input type="button" value="목록으로" class="btn-secondary-full" onclick="location.href='userList.do'">
<%
} 
%>
        </div>
    </form>
</div>
<%

    // Case 4: [기타 메시지]
	} else if (message != null) {
%>
    <%-- [유지] 550px 표준 컨테이너 사용 --%>
    <div class="user-form-container result-message">
        <h2>처리 결과</h2>
        <hr style="margin-bottom: 24px;">
        <p><%= message %></p>
        <br>
<%
    if("ADMIN".equals(userRole)) {
%>
        <a href="userList.do">[회원 목록으로 가기]</a>
<%
    }
%>
        <a href="index.jsp">[홈으로 가기]</a>
    </div>
<%
    // Case 5: [잘못된 접근]
	} else {
		response.sendRedirect("index.jsp");
	}
%>
    
    </div> </main> <footer class="main-footer">
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