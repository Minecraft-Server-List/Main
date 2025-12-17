<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, com.example.minecraft.dto.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사용자 관리 결과 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/form-style.css">
</head>
<body>

<%@ include file = "../header.jsp" %>

<%
    String deletedEmail = (String) request.getAttribute("deletedEmail");
    UserDTO oldUser = (UserDTO) request.getAttribute("oldUserData");
    UserDTO newUser = (UserDTO) request.getAttribute("newUserData");
    UserDTO userToEdit = (UserDTO) request.getAttribute("userToEdit");
    String message = (String) request.getAttribute("message");
    String userRole = (String) session.getAttribute("userRole");
%>

<main class="form-page-main">
    <div class="container">

<%
    // Case 1: [삭제 성공]
	if (deletedEmail != null) {
%>
    <div class="user-form-container result-message">
        <h2>계정 삭제 완료</h2>
        <hr style="margin-bottom: 24px;">
        <p><font color="red">'<%= deletedEmail %>'</font> 계정이 삭제되었습니다.</p>
<% if("ADMIN".equals(userRole)) { %>
        <%-- [변경] 목록 이동 경로 수정 --%>
        <a href="${pageContext.request.contextPath}/user/list">[회원 목록으로 가기]</a>
<% } %>
        <a href="${pageContext.request.contextPath}/index.page">[홈으로 가기]</a>
    </div>

<%
    // Case 2: [수정 성공]
	} else if (oldUser != null && newUser != null) {
%>
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
                <% if (!newUser.getPassword().equals(oldUser.getPassword())) { %>
                    <td><font color="blue"><strong>(새 비밀번호로 변경됨)</strong></font></td>
                <% } else { %>
                    <td>(변경 없음)</td>
                <% } %>
            </tr>
        </table>
        
        <div class="user-form-buttons">
<% if("ADMIN".equals(userRole)) { %>
            <%-- [변경] 목록 버튼 경로 수정 --%>
            <input type="button" value="회원 목록으로" class="btn btn-secondary" onclick="location.href='${pageContext.request.contextPath}/user/list'">
            <input type="button" value="홈으로 가기" class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/index.page'">
<% } else { %>
            <input type="button" value="홈으로 가기" class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/index.page'">
<% } %>
        </div>
    </div>
<%
// Case 3: [검색 성공] (수정 폼)
} else if (userToEdit != null) {
%>
<div class="user-form-container">
    <h2>회원 정보 수정</h2>
    <hr style="margin-bottom: 24px;">
    
    <%-- [변경] form action 경로 수정 --%>
    <form action="${pageContext.request.contextPath}/user/update" method="post">
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
            <label for="password">새 비밀번호</label>
            <input type="password" id="password" name="password" placeholder="변경할 경우에만 입력하세요">
        </div>
        <div class="user-form-group">
            <label for="role">역할</label>
<% if ("ADMIN".equals(userRole)) { %>
                <select name="role" id="role">
                    <option value="ADMIN" <%= "ADMIN".equals(userToEdit.getRole()) ? "selected" : "" %>>ADMIN</option>
                    <option value="USER"  <%= "USER".equals(userToEdit.getRole()) ? "selected" : "" %>>USER</option>
                </select>
<% } else { %>
                <input type="text" value="<%= userToEdit.getRole() %>" readonly>
                <input type="hidden" name="role" value="<%= userToEdit.getRole() %>">
<% } %>
        </div>
        <div class="user-form-group">
            <input type="submit" value="정보 수정하기">
<% if("ADMIN".equals(userRole)) { %>
            <%-- [변경] 목록 버튼 경로 수정 --%>
            <input type="button" value="목록으로" class="btn-secondary-full" onclick="location.href='${pageContext.request.contextPath}/user/list'">
<% } %>
        </div>
    </form>
</div>
<%
    // Case 4: [기타 메시지]
	} else if (message != null) {
%>
    <div class="user-form-container result-message">
        <h2>처리 결과</h2>
        <hr style="margin-bottom: 24px;">
        <p><%= message %></p>
        <br>
<% if("ADMIN".equals(userRole)) { %>
        <%-- [변경] 목록 링크 수정 --%>
        <a href="${pageContext.request.contextPath}/user/list">[회원 목록으로 가기]</a>
<% } %>
        <a href="${pageContext.request.contextPath}/index.page">[홈으로 가기]</a>
    </div>
<%
    // Case 5: [잘못된 접근]
	} else {
		response.sendRedirect(request.getContextPath() + "/index.page");
	}
%>
    </div> 
</main> 

<%@ include file="../footer.jsp" %>

</body>
</html>