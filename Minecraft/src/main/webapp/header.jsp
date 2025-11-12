<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // 1. 세션에서 로그인 정보를 가져옵니다. (이름, 역할, 이메일)
    String userName_header = (String) session.getAttribute("userName");
    String userRole_header = (String) session.getAttribute("userRole");
    String userEmail_header = (String) session.getAttribute("userEmail");
%>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
<link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">


<header class="main-header">
    <nav class="container main-nav">
        
        <div class="nav-left">
            <a href="${pageContext.request.contextPath}/index.jsp" class="logo">CraftConnect</a>
            <ul class="nav-links">
                <li><a href="#">Servers</a></li>
                <li><a href="#">Community</a></li>
                <li><a href="#">News</a></li>
                <li><a href="#">Support</a></li>
                
                <%-- [로직 적용 1] 관리자(ADMIN)일 경우에만 '관리자 메뉴' 표시 --%>
                <%
                    if ("ADMIN".equals(userRole_header)) {
                %>
                        <li>
                            <a href="${pageContext.request.contextPath}/userList.do" style="color: #d9534f; font-weight: 700;">
                                [관리자 메뉴]
                            </a>
                        </li>
                <%
                    }
                %>
            </ul>
        </div>
        
        <div class="nav-right">
            <div class="header-search">
                <i class='bx bx-search'></i>
                <input type="search" placeholder="Search">
            </div>
            
            <button class="btn-add-server">Add Server</button>

            <%-- [로직 적용 2] 로그인 상태에 따라 다른 메뉴 표시 --%>
            <%
                if (userName_header == null) {
                    // --- 1. 로그아웃 상태 ---
                    // '로그인'(login.jsp)과 '회원가입'(registerForm.jsp) 버튼 표시
            %>
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-header-login">Login</a>
                <a href="${pageContext.request.contextPath}/registerForm.jsp" class="btn-header-register">Register</a>
            <%
                } else {
                    // --- 2. 로그인 상태 ---
                    // '마이페이지' 아이콘과 '로그아웃' 버튼 표시
            %>
                <a href="${pageContext.request.contextPath}/searchUser.do?email=<%= userEmail_header %>" class="user-profile" title="My Page">
                    <img src="https://placehold.co/40x40/9a9a9a/ffffff?text=<%= userName_header.substring(0, 1).toUpperCase() %>" alt="User Profile">
                </a>
                
                <a href="${pageContext.request.contextPath}/logout.do" class="btn-header-logout">Logout</a>
            <%
                }
            %>
        </div>
    </nav>
</header>