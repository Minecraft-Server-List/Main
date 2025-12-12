<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CraftConnect - Find Your Perfect Minecraft Server</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login-modal.css">

    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            console.log("서버 목록 페이지 로드됨. 서버 상태는 DB에서 직접 제공됩니다.");
        });
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header.jsp" %>

<main class="index-main">

    <section class="hero-new">
        <div class="hero-gradient-overlay"></div>

        <div class="container hero-content-new">
            <h1 class="hero-title-new">
                다양한 서버들이 모이는 곳 <span class="md-block-hide"></span>
            </h1>

            <p class="hero-subtitle-new">
                저희 CraftConnect 에서는 다양한 장르의 서버들을 제공합니다. <br>
                매일 같은 게임을 하는게 지루하셨다면? 색다른 서버를 찾아 즐겨보세요!
            </p>

            <div class="search-bar-new">
                <%-- 🌟 수정: 폼 action을 /serverList로, method를 GET으로 설정 --%>
                <form action="${pageContext.request.contextPath}/serverList" method="GET" class="search-input-group">
                    <i class='bx bx-search search-icon-new'></i>
                    <input
                            type="text"
                            name="query"  <%-- 🌟 name="query" 추가: 서버리스트에서 이 파라미터를 받습니다. --%>
                            placeholder="서버 검색하기"
                            class="search-input-field"
                    />
                    <%-- type="submit"으로 변경하여 폼을 제출하도록 합니다. --%>
                    <button class="btn-search-new" type="submit">검색</button>
                </form>
            </div>

            <div class="stats-row-new">
                <div class="stat-item-new">
                    <i class='bx bxs-server stat-icon-new'></i>
                    <span class="stat-value-new">15,000+</span>
                    <span class="stat-label-new">Servers</span>
                </div>
                <div class="stat-item-new">
                    <i class='bx bxs-user-detail stat-icon-new'></i>
                    <span class="stat-value-new">2M+</span>
                    <span class="stat-label-new">Players</span>
                </div>
                <div class="stat-item-new">
                    <i class='bx bx-globe stat-icon-new'></i>
                    <span class="stat-value-new">150+</span>
                    <span class="stat-label-new">Countries</span>
                </div>
            </div>
        </div>
    </section>

    <section class="featured-servers-new">
        <div class="container">
            <div class="text-center-new">
                <h2 class="section-title-new">추천 서버</h2>
                <p class="section-subtitle-new">
                    저희 CraftConnect 에서는 추천하는 서버들입니다.
                </p>
            </div>

            <div class="featured-card-grid">

                <c:forEach var="server" items="${serverList}" end="2">
                    <div class="server-card-featured" data-domain="${server.domain}">

                        <div class="card-image-container-featured">

                            <c:choose>
                                <c:when test="${not empty server.serverImage && not empty server.serverImage.fileName}">
                                    <img
                                            src="${pageContext.request.contextPath}/upload/server_images/${server.serverImage.fileName}"
                                            alt="${server.name}"
                                            class="card-image-featured"
                                    />
                                </c:when>
                                <c:otherwise>
                                    <img
                                            src="${pageContext.request.contextPath}/static/images/default_server_icon.png"
                                            alt="${server.name}"
                                            class="card-image-featured"
                                    />
                                </c:otherwise>
                            </c:choose>

                            <div class="card-category-badge-featured">
                                <c:out value="${server.category}" default="없음" />
                            </div>
                        </div>

                        <div class="card-content-featured">
                            <h3 class="card-title-featured">${server.name}</h3>

                            <div class="card-divider-featured"></div>

                            <div class="card-meta-featured">
                                <div class="meta-item-featured player-count">
                                    <i class='bx bxs-user-detail'></i>
                                    <c:choose>
                                        <c:when test="${server.status eq 'ONLINE'}">
                                            <span>${server.onlinePlayers} / ${server.maxPlayers}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span>Offline</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="meta-item-featured server-version">
                                    <i class='bx bx-joystick'></i>
                                    <span class="version-text">${server.version}</span>
                                </div>
                            </div>
                            <a href="${pageContext.request.contextPath}/server.do?action=view&id=${server.serverId}" class="card-link-overlay-featured"></a>
                        </div>
                    </div>
                </c:forEach>

            </div>

            <div class="btn-group-center">
                <button class="btn-view-all" onclick="window.location.href='${pageContext.request.contextPath}/serverList'">
                    View All Servers
                </button>
            </div>
        </div>
    </section>

</main>
<%@ include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>