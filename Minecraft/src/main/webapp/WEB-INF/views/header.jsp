<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String userName_header = (String) session.getAttribute("userName");
    String userRole_header = (String) session.getAttribute("userRole");
    String userEmail_header = (String) session.getAttribute("userEmail");
%>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
<link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">


<header class="main-header">
    <nav class="container main-nav">
        
        <div class="nav-left">
            <%-- [수정] index.jsp -> index.page --%>
            <a href="${pageContext.request.contextPath}/index.page" class="logo">CraftConnect</a>
            <ul class="nav-links">
                <li><a href="#">Servers</a></li>
                <li><a href="#">Community</a></li>
                <li><a href="#">News</a></li>
                <li><a href="#">Support</a></li>
                
                <%-- [유지] 관리자 메뉴는 데이터를 가져와야 하므로 .do 유지 --%>
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

            <a href="${pageContext.request.contextPath}/serverAdd.page" class="btn-add-server">Add Server</a>

            <%
                if (userName_header == null) {
            %>
                <%-- [수정] 직접 경로 -> PageController 경로 (.page) --%>
                <a href="${pageContext.request.contextPath}/login.page" class="btn-header-login">Login</a>
                <a href="${pageContext.request.contextPath}/register.page" class="btn-header-register">Register</a>
            <%
                } else {
            %>
                <%-- [유지] 마이페이지/로그아웃은 기능 수행이 필요하므로 .do 유지 --%>
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