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

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const API_URL = '${pageContext.request.contextPath}/api/server/status';

            // 1. AJAX 요청 함수
            function fetchServerStatuses() {
                fetch(API_URL)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('API 호출 실패: ' + response.statusText);
                        }
                        return response.json();
                    })
                    .then(serverList => {
                        // 2. JSON 데이터로 화면 업데이트
                        serverList.forEach(updateServerCard);
                    })
                    .catch(error => {
                        console.error("서버 상태 업데이트 오류:", error);
                    });
            }

            // 3. 개별 서버 카드 업데이트 로직 (🚨 Null 안전하게 수정됨)
            function updateServerCard(server) {
                // 해당 도메인을 가진 카드 DOM 요소 찾기
                const cardElement = document.querySelector(`.server-card[data-domain="${server.domain}"]`);
                if (!cardElement) return;

                // 🚨 Optional Chaining (?. ) 및 Nullish Coalescing (??) 적용
                // server.serverStatus가 없으면 (API 응답에 없으면) false/0으로 처리합니다.
                const isOnline = server.serverStatus?.online ?? false;
                const onlinePlayers = server.serverStatus?.players?.online ?? 0;
                const maxPlayers = server.serverStatus?.players?.max ?? 0;

                // version 정보가 API에 없다면, JSP에서 미리 받은 DB 버전 정보를 사용합니다.
                const version = isOnline && server.serverStatus?.version?.nameClean ? server.serverStatus.version.nameClean : cardElement.querySelector('[data-version]').textContent;

                // 상태 배지 업데이트
                const badge = cardElement.querySelector('[data-status-badge]');
                if (badge) {
                    badge.classList.remove('offline', 'online', 'initial-status');
                    badge.classList.add(isOnline ? 'online' : 'offline');

                    // isOnline이 false인 경우에도 Loading... 대신 Offline으로 표시
                    badge.querySelector('[data-status-text]').textContent = isOnline ? 'ONLINE' : 'OFFLINE';
                    badge.querySelector('span').textContent = isOnline ? 'ONLINE' : 'OFFLINE';
                }

                // 플레이어 수 업데이트
                const playerCountElement = cardElement.querySelector('[data-player-count]');
                if (playerCountElement) {
                    if (isOnline) {
                        playerCountElement.innerHTML = `${onlinePlayers} / ${maxPlayers}`;
                    } else {
                        // 오프라인이거나 serverStatus 정보가 없을 때
                        playerCountElement.innerHTML = `Offline`;
                    }
                }

                // 버전 업데이트
                const versionElement = cardElement.querySelector('[data-version]');
                if (versionElement) {
                    versionElement.textContent = version;
                }

                // 게이지 바 업데이트
                const fillElement = cardElement.querySelector('.progress .fill');
                if (fillElement) {
                    let percentage = 0;
                    if (isOnline && maxPlayers > 0) {
                        percentage = (onlinePlayers / maxPlayers) * 100;
                    }
                    fillElement.style.width = `${percentage}%`;
                }
            }

            // 4. 페이지 로드 후 즉시 상태 업데이트 시작
            fetchServerStatuses();

            // 5. 60초마다 상태를 주기적으로 업데이트 (선택 사항)
            // setInterval(fetchServerStatuses, 60000);
        });
    </script>
</head>

<body>

<%@ include file="/WEB-INF/views/header.jsp" %>

<section class="server-list-hero">
    <div class="container hero-inner-list">
        <h1 class="hero-title">마인크래프트 서버 목록</h1>
        <p class="hero-sub-list">
            <c:out value="${fn:length(serverList)}" />개의 서버를 찾았습니다
        </p>

        <form action="${pageContext.request.contextPath}/serverList" method="GET" class="hero-search-list">
            <i class='bx bx-search'></i>
            <input type="text"
                   name="query"                  placeholder="서버 이름이나 설명으로 검색..."
                   value="${searchQuery != null ? searchQuery : ''}"> <button type="submit" style="display:none;"></button>
        </form>

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
            <%-- 🚨 서버 상태를 제외한 모든 정보는 DB에서 가져오므로 안전합니다. --%>
            <c:forEach var="server" items="${serverList}">
                <div class="server-card" data-domain="${server.domain}">

                    <div class="thumb"
                         style="background-image:url(
                         <c:choose>
                             <%-- 🚨 이미지 Null 체크 강화 (server.serverImage가 null인지 먼저 확인) --%>
                         <c:when test="${not empty server.serverImage && not empty server.serverImage.fileName}">
                                 '${pageContext.request.contextPath}/upload/server_images/${server.serverImage.fileName}'
                         </c:when>
                         <c:otherwise>
                                 '${pageContext.request.contextPath}/static/images/default_server_icon.png'
                         </c:otherwise>
                         </c:choose>
                                 );">

                        <div class="badge-left">${server.category}</div>
                        <div class="badge-right initial-status offline" data-status-badge>
                            ● <span data-status-text>Loading...</span>
                        </div>
                    </div>

                    <div class="card-body">
                        <h2>${server.name}</h2>
                        <p class="desc">${server.description}</p>

                        <div class="tags">
                            <span class="main-category-tag">${server.category}</span>
                            <span class="sub-tag">Survival</span>
                            <span class="sub-tag">Economy</span>
                        </div>

                        <div class="player-row">
                            <span>플레이어</span>
                            <b data-player-count>
                                <span class="loading-dots">...</span>
                            </b>
                        </div>

                        <div class="progress">
                            <div class="fill" style="width: 0%;"></div>
                        </div>

                        <div class="bottom-row">
                            <div class="ver" data-version>${server.version}</div>
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