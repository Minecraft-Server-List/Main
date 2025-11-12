<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, com.example.minecraft.User.*, java.time.format.DateTimeFormatter"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원목록 - CraftConnect</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/form-style.css">
    
</head>
<body>

<%@ include file = "header.jsp" %>

<%
	ArrayList<UserDTO> uList = (ArrayList<UserDTO>)request.getAttribute("allList");
%>
<%!
    // yyyy-MM-dd 형식 포맷터
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
        <a href="index.jsp">[홈으로 가기]</a>
    </div>
<%
	}else{
%>
    <div class="wide-result-container">
        <h2>전체 회원 목록</h2>
        
        <%-- 
           .server-table 스타일은 tempheader.jsp -> style.css에서 가져옵니다.
           전용 스타일은 form-style.css에서 .server-table을 구체적으로 타겟팅합니다.
        --%>
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
<%		
		for(UserDTO dto : uList){
%>
                <tr>
                    <td><%= dto.getUserId()  %></td>
                    
                    <%-- [수정] '이름' 열에 전용 클래스 추가 --%>
                    <td class="user-list-name-cell"><%= dto.getName()  %></td>
                    
                    <td><%= dto.getEmail()  %></td>
                    <td><%= dto.getRole()  %></td>
                    <td><%= dto.getCreatedAt().format(dtf)  %></td>
                    
                    <td>
                        <%-- [수정] '관리' 링크에 버튼 클래스 추가 --%>
                        <a href="searchUser.do?email=<%= dto.getEmail() %>" class="btn-action-edit">[수정]</a>
                        <a href="deleteUser.do?userId=<%= dto.getUserId() %>" class="btn-action-delete">[삭제]</a>
                    </td>
                </tr>
<%
		}
%>
            </tbody>
        </table>
    </div>
<%
	}
%>
    </div>
</main>

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