<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, com.example.minecraft.User.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사용자 관리 결과</title>
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


	// --- 3. 로직 분기 ---
	
    // Case 1: [삭제 성공]
	if (deletedEmail != null) {
%>
    <h2>계정 삭제 완료</h2>
    <hr>
    <h3><font color="red"><%= deletedEmail %> 계정이 삭제되었습니다.</font></h3>
    <br>
<%
    if("ADMIN".equals(userRole)) {
%>
    <a href="userList.do">[회원 목록으로 가기]</a>
<%
    } 
%>
    <a href="index.jsp">[홈으로 가기]</a>

<%
    // Case 2: [수정 성공]
	} else if (oldUser != null && newUser != null) {
%>
    <h2>회원 정보 수정 완료</h2>
    <hr>
    <p>사용자 정보가 성공적으로 수정되었습니다.</p>
    
    <table border="1" style="width: 600px; text-align: center;">
        <tr style="background-color: #eee;">
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
    <br>
<%
    if("ADMIN".equals(userRole)) {
%>
    <a href="userList.do">[회원 목록으로 가기]</a>
<%
    } 
%>
    <a href="index.jsp">[홈으로 가기]</a>

<%
// Case 3: [검색 성공] (수정 폼)
} else if (userToEdit != null) {
%>
<h2>회원 정보 수정 (검색 결과)</h2>
<hr>
<form action="updateUser.do" method="post">
    
    <%-- 수정 대상의 ID는 POST로 전송해야 하므로 hidden 필드로 유지 --%>
    <input type="hidden" name="userId" value="<%= userToEdit.getUserId() %>">
    
    <table border="1">
        <tr>
            <td>사용자 ID (수정불가)</td>
            <td><%= userToEdit.getUserId() %></td>
        </tr>
         <tr>
            <td>가입일시 (수정불가)</td>
            <td><%= userToEdit.getCreatedAt() %></td>
        </tr>
         <tr>
            <td>이름</td>
            <td><input type="text" name="name" value="<%= userToEdit.getName() %>"></td>
        </tr>
        <tr>
            <td>이메일</td>
            <td><input type="email" name="email" value="<%= userToEdit.getEmail() %>"></td>
        </tr>
        <tr>
            <td>새 비밀번호</td>
            <td><input type="password" name="password" placeholder="변경할 경우에만 입력하세요"></td>
        </tr>

        <%-- ▼▼▼ 수정된 '역할' 섹션 ▼▼▼ --%>
        <tr>
            <td>역할 (ADMIN / USER)</td>
            <td>
<%
// 현재 로그인한 사용자의 권한(userRole)을 확인합니다.
if ("ADMIN".equals(userRole)) {
    // [ADMIN인 경우] : 역할 수정이 가능한 <select> 드롭다운을 표시합니다.
%>
                <select name="role">
                    <option value="ADMIN" <%= "ADMIN".equals(userToEdit.getRole()) ? "selected" : "" %>>ADMIN</option>
                    <option value="USER"  <%= "USER".equals(userToEdit.getRole()) ? "selected" : "" %>>USER</option>
                </select>
<%
} else {
    // [USER인 경우] : 역할을 수정할 수 없도록 단순 텍스트로 표시합니다.
%>
                <%= userToEdit.getRole() %>
                
                <%-- 
                    폼 전송(submit) 시, 기존 역할 값이 누락되지 않고 
                    그대로 서버로 전송되도록 hidden input을 추가합니다.
                --%>
                <input type="hidden" name="role" value="<%= userToEdit.getRole() %>">
<%
} // if-else 종료
%>
            </td>
        </tr>
        <%-- ▲▲▲ '역할' 섹션 수정 완료 ▲▲▲ --%>

    </table>
    <br>
    
    <input type="submit" value="정보 수정하기">
<%
// 목록 버튼은 ADMIN에게만 표시
if("ADMIN".equals(userRole)) {
%>
    <input type="button" value="목록으로" onclick="location.href='userList.do'">
<%
} 
%>
</form>

<%

    // Case 4: [기타 메시지]
	} else if (message != null) {
%>
    <h2>처리 결과</h2>
    <hr>
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
<%
    // Case 5: [잘못된 접근]
	} else {
		response.sendRedirect("index.jsp");
	}
%>

</body>
</html>