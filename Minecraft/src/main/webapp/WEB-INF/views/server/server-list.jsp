<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>마인크래프트 서버 상태 목록 - CraftConnect</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
</head>
<body>

<%@ include file="/WEB-INF/views/header.jsp" %>

<main class="page-main">
    <div class="container">

        <section class="top-servers">
            <h2><i class='bx bx-server' style="margin-right: 8px;"></i>등록된 서버 목록</h2>

            <c:choose>
                <c:when test="${not empty serverList}">

                    <table class="server-table">
                        <thead>
                        <tr>
                            <th>Image</th> <th>Server Name</th>
                            <th>Domain</th>
                            <th>Version</th>
                            <th>Players</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="server" items="${serverList}">
                            <tr>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty server.serverImage}">
                                            <%-- Image DTO의 fileName 필드를 사용하며, /upload/server_images/ 경로는 Tomcat 설정에 매핑되어야 합니다. --%>
                                            <img src="${pageContext.request.contextPath}/upload/server_images/${server.serverImage.fileName}"
                                                 alt="${server.name} 이미지"
                                                 style="width: 50px; height: 50px; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            [No Image]
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                    <%-- 1. Server Name (DB 이름 + .server-name 클래스) --%>
                                <td class="server-name"><c:out value="${server.name}" /></td>

                                    <%-- 2. Domain (DB 호스트 주소) --%>
                                <td><c:out value="${server.domain}" /></td>

                                    <%-- 3. Version (API 상태 정보) --%>
                                <td>
                                    <c:out value="${server.serverStatus.version.nameClean}" default="1.20.1" />
                                </td>

                                    <%-- 4. Players (메인 페이지 형식: 120/200) --%>
                                <td>
                                    <c:out value="${server.serverStatus.players.online}" default="0" />
                                    <span class="player-count-max">/<c:out value="${server.serverStatus.players.max}" default="0" />
                                    </span>
                                </td>

                                    <%-- 5. Status (status-online/server-offline 클래스 사용) --%>
                                <td>
                                    <c:choose>
                                        <c:when test="${server.serverStatus.online}">
                                            <span class="status-online">Online</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="server-offline">Offline</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p style="text-align: center; color: #777; margin-top: 50px;">
                        현재 등록된 서버가 없습니다.
                        <a href="${pageContext.request.contextPath}/serverAdd.page">새 서버를 등록</a>해주세요.
                    </p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>