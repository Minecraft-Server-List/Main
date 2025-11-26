<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, com.example.minecraft.dto.*, java.time.format.DateTimeFormatter"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원목록 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/form-style.css">
</head>
<body>

<%@ include file = "header.jsp" %>

<%
	ArrayList<UserDTO> uList = (ArrayList<UserDTO>)request.getAttribute("allList");
%>
<%!
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
%>

<main class="form-page-main">
    <div class="container">
<%	
	if(uList == null || uList.isEmpty()){
%>
    <div class="user-form-container result-message">
        <h2>회원 목록</h2>
        <hr style="margin-bottom: 24px;">
        <p>등록된 회원이 없습니다.</p>
        <%-- [변경] 홈 이동 --%>
        <a href="${pageContext.request.contextPath}/index.page">[홈으로 가기]</a>
    </div>
<%
	}else{
%>
    <div class="wide-result-container">
        <h2>전체 회원 목록</h2>
        <table class="server-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>이름</th>
                    <th>이메일</th>
                    <th>역할</th>
                    <th>가입일시</th>
                    <th>관리</th>
                </tr>
            </thead>
            <tbody>
<% for(UserDTO dto : uList){ %>
                <tr>
                    <td><%= dto.getUserId()  %></td>
                    <td class="user-list-name-cell"><%= dto.getName()  %></td>
                    <td><%= dto.getEmail()  %></td>
                    <td><%= dto.getRole()  %></td>
                    <td><%
                        // 3. 가입일시 (created_at) Null 체크 로직 (수정된 부분)
                        // dto.getCreatedAt()이 null이 아닌 경우에만 format()을 호출합니다.
                        if (dto.getCreatedAt() != null) {
                            out.print(dto.getCreatedAt().format(dtf));
                        } else {
                            // 값이 null이면 사용자에게 "N/A" (Not Available) 출력
                            out.print("N/A");
                        }
                    %></td>
                    <td>
                        <%-- [유지] 기능 수행(수정/삭제)은 서블릿(.do) 호출 --%>
                        <a href="searchUser.do?email=<%= dto.getEmail() %>" class="btn-action-edit">[수정]</a>
                        <a href="deleteUser.do?userId=<%= dto.getUserId() %>" class="btn-action-delete">[삭제]</a>
                    </td>
                </tr>
<% } %>
            </tbody>
        </table>
    </div>
<%
	}
%>
    </div>
</main>

<%@ include file="footer.jsp" %>

</body>
</html>