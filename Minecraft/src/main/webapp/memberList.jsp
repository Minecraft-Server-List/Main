<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, com.example.minecraft.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원목록</title>
</head>
<body>
<%@ include file = "header.jsp" %>

<%
	ArrayList<UserDTO> uList = (ArrayList<UserDTO>)request.getAttribute("allList");
	
	if(uList == null || uList.isEmpty()){
%>
    <h2>등록된 회원이 없습니다.</h2>
    <a href="index.jsp">[홈으로 가기]</a>
<%
	}else{
%>
    <h2>전체 회원 목록</h2>
    <table border="1">
        <tr>
            <td>ID</td>
            <td>이름</td>
            <td>이메일</td>
            <td>역할</td>
            <td>가입일시</td>
            <td>관리</td>
        </tr>
<%		
		for(UserDTO dto : uList){
%>
			<tr>
				<td><%= dto.getUserId()  %></td>
				<td><%= dto.getName()  %></td>
				<td><%= dto.getEmail()  %></td>
				<td><%= dto.getRole()  %></td>
				<td><%= dto.getCreatedAt()  %></td>
                <td>
                    <a href="searchUser.do?email=<%= dto.getEmail() %>">[수정]</a>
                    
                    <a href="deleteUser.do?userId=<%= dto.getUserId() %>">[삭제]</a>
                </td>
			</tr>
<%
		}
%>
	</table>
<%
	}
%>

</body>
</html>