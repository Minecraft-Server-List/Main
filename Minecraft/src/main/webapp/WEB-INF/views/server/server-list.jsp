<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>마인크래프트 서버 상태 목록 - CraftConnect</title>

    <%-- 메인 페이지와 동일한 스타일시트 구조를 따름 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">

    <%-- Boxicons 아이콘 사용을 위해 추가 --%>
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
</head>
<body>

<%@ include file="/WEB-INF/views/header.jsp" %>

<main class="page-main">
    <div class="container">

        <%-- 메인 페이지의 Top Servers 섹션 클래스 구조를 따릅니다. --%>
        <section class="top-servers">
            <h2><i class='bx bx-server' style="margin-right: 8px;"></i>등록된 서버 목록</h2>

            <c:choose>
                <c:when test="${not empty serverList}">

                    <%-- 메인 페이지의 Top Servers 테이블 클래스 (.server-table) 사용 --%>
                    <table class="server-table">
                        <thead>
                        <tr>
                            <th>Server Name</th>
                            <th>Domain</th>
                            <th>Version</th>
                            <th>Players</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="server" items="${serverList}">
                            <tr>

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
                                            <%-- Offline은 CSS에서 빨간색 계열로 정의됨 --%>
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