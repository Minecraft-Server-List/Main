<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 🚨 중요: JSTL 사용을 위해 Taglib 선언을 추가합니다. --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>마인크래프트 서버 상태 목록 - CraftConnect</title>
    <%-- form.jsp와 동일한 스타일시트 구조를 따릅니다. --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/table-style.css">
</head>
<body>

<%-- 🚩 header.jsp 인클루드 --%>
<%@ include file="/WEB-INF/views/header.jsp" %>

<main class="page-main">
    <div class="container">
        <h1>✅ 마인크래프트 서버 상태 목록</h1>
        <p style="margin-bottom: 20px;">데이터베이스에 등록된 서버의 실시간 상태를 표시합니다.</p>

        <c:choose>
            <c:when test="${not empty serverList}">
                <table class="data-table" border="1">
                    <thead>
                    <tr>
                        <th>DB 이름</th>
                        <th>도메인</th>
                        <th>상태</th>
                        <th>현재 접속자</th>
                        <th>최대 접속자</th>
                        <th>버전 정보</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="server" items="${serverList}">
                        <tr>
                            <td><c:out value="${server.name}" /></td>
                            <td><c:out value="${server.domain}" /></td>

                            <td>
                                <c:choose>
                                    <c:when test="${server.serverStatus.online}">
                                        <span style="color: green; font-weight: bold;">ONLINE</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: red;">OFFLINE</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <c:out value="${server.serverStatus.players.online}" default="N/A" />
                            </td>
                            <td>
                                <c:out value="${server.serverStatus.players.max}" default="N/A" />
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p style="text-align: center; color: #777;">현재 등록된 서버가 없습니다. <a href="${pageContext.request.contextPath}/serverAdd.page">새 서버를 등록</a>해주세요.</p>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%-- 🚩 footer.jsp 인클루드 --%>
<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>