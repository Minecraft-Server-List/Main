<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8" />
    <title>마인크래프트 서버 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/footer.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/server-list.css">
</head>

<body>

<%@ include file="/WEB-INF/views/header.jsp" %>

<section class="server-list-hero">
    <div class="container hero-inner-list">
        <h1 class="hero-title">마인크래프트 서버 목록</h1>
        <p class="hero-sub-list">
            <c:out value="${fn:length(serverList)}" />개의 서버를 찾았습니다
        </p>

        <div class="hero-search-list">
            <i class='bx bx-search'></i>
            <input type="text" placeholder="서버 이름이나 설명으로 검색...">
        </div>
    </div>
</section>

<div class="container content-grid">

    <aside class="sidebar">

        <div class="sidebar-box category-section">
            <h3 class="section-title">카테고리</h3>
            <ul>
                <li class="active"><i class='bx bx-grid-alt'></i> 전체</li>
                <li><i class='bx bx-leaf'></i> Survival</li>
                <li><i class='bx bx-pen'></i> Creative</li>
                <li><i class='bx bx-target-lock'></i> PvP</li>
                <li><i class='bx bx-cloud-lightening'></i> Skyblock</li>
                <li><i class='bx bx-lock-alt'></i> Prison</li>
                <li><i class='bx bx-shield'></i> Faction</li>
                <li><i class='bx bx-network-chart'></i> Network</li>
                <li><i class='bx bx-cube-alt'></i> Modded</li>
                <li><i class='bx bx-run'></i> Roleplay</li>
            </ul>
        </div>

        <div class="sidebar-box version-filter-section">
            <h3 class="section-title">버전</h3>
            <ul class="version-list">
                <li class="active-version">모든 버전</li>
                <li>1.20.4</li>
                <li>1.20.3</li>
                <li>1.20.2</li>
                <li>1.19.4</li>
                <li>1.18.2</li>
            </ul>
        </div>

        <div class="sidebar-box stats-section stats-gradient-box">
            <h3 class="section-title">통계</h3>
            <div class="stats-data">
                <div class="stat-item">
                    <span>총 서버</span>
                    <b class="stat-value">15,234</b>
                </div>
                <div class="stat-item">
                    <span>온라인 플레이어</span>
                    <b class="stat-value">2.1M</b>
                </div>
                <div class="stat-item">
                    <span>오늘 투표</span>
                    <b class="stat-value">45,678</b>
                </div>
            </div>
        </div>

    </aside>

    <section class="server-list">

        <div class="list-header">
            <span>총 <b>${fn:length(serverList)}</b>개 서버</span>

            <select>
                <option>인기순</option>
                <option>플레이어 많은 순</option>
                <option>최신 서버 순</option>
            </select>
        </div>

        <div class="card-grid">

            <c:forEach var="server" items="${serverList}">
                <div class="server-card">

                    <div class="thumb"
                         style="background-image:url(
                         <c:choose>
                         <c:when test="${not empty server.serverImage.fileName}">
                                 '${pageContext.request.contextPath}/upload/server_images/${server.serverImage.fileName}'
                         </c:when>
                         <c:otherwise>
                                 '${pageContext.request.contextPath}/static/images/default_server_icon.png'
                         </c:otherwise>
                         </c:choose>
                                 );">

                        <div class="badge-left">Network</div>
                        <div class="badge-right ${server.serverStatus.online ? 'online' : 'offline'}">
                            ● <c:out value="${server.serverStatus.online ? 'ONLINE' : 'OFFLINE'}" />
                        </div>
                    </div>

                    <div class="card-body">
                        <h2>${server.name}</h2>
                        <p class="desc">서버 설명이 여기에 들어갑니다.</p>

                        <div class="tags">
                            <span>Minigames</span>
                            <span>Survival</span>
                            <span>Economy</span>
                        </div>

                        <div class="player-row">
                            <span>플레이어</span>
                            <b>${server.serverStatus.players.online} / ${server.serverStatus.players.max}</b>
                        </div>

                        <div class="progress">
                            <c:set var="fillPercentage">
                                <c:choose>
                                    <%-- Case 1: 최대 인원수가 0이거나 데이터가 없을 때 (분모 오류 방지) --%>
                                    <c:when test="${server.serverStatus.players.max == 0 || empty server.serverStatus.players.max}">
                                        0
                                    </c:when>
                                    <%-- Case 2: 온라인 인원수가 0일 때 --%>
                                    <c:when test="${server.serverStatus.players.online == 0}">
                                        0
                                    </c:when>
                                    <%-- Case 3: 정상적인 계산 --%>
                                    <c:otherwise>
                                        ${(server.serverStatus.players.online * 100 / server.serverStatus.players.max)}
                                    </c:otherwise>
                                </c:choose>
                            </c:set>

                            <div class="fill"
                                 style="width: ${fillPercentage}%;"></div>
                        </div>

                        <div class="bottom-row">
                            <div class="ver">1.20.4</div>
                            <div class="votes">★ 0 votes</div>
                        </div>

                        <a href="${pageContext.request.contextPath}/server.do?action=view&id=${server.serverId}"
                           class="view-btn">서버 정보 보기</a>
                    </div>
                </div>
            </c:forEach>

        </div>

    </section>

</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>