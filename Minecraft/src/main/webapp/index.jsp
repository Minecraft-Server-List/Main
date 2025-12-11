<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- 🚨 JSTL Functions 태그 라이브러리 추가 --%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CraftConnect - Find Your Perfect Minecraft Server</title>

    <%-- 🚨 CSS 파일 로드 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/footer.css">

    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>

    <%-- 🚨 AJAX 비동기 로딩 JavaScript 추가 --%>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 이 API는 ServerStatusApiController에서 제공하는 JSON API URL과 동일해야 합니다.
            const API_URL = '${pageContext.request.contextPath}/api/server/status';

            function fetchServerStatuses() {
                fetch(API_URL)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('API 호출 실패: ' + response.statusText);
                        }
                        return response.json();
                    })
                    .then(serverList => {
                        // 페이지에 있는 모든 서버 카드 업데이트
                        serverList.slice(0, 3).forEach(updateServerCard);
                    })
                    .catch(error => {
                        console.error("서버 상태 업데이트 오류:", error);
                    });
            }

            function updateServerCard(server) {
                // 이 함수는 server-list.jsp에 있는 updateServerCard와 동일합니다.
                const cardElement = document.querySelector(`.server-card-featured[data-domain="${server.domain}"]`);
                if (!cardElement) return;

                const isOnline = server.serverStatus && server.serverStatus.online;
                const onlinePlayers = isOnline ? (server.serverStatus.players ? server.serverStatus.players.online : 0) : 0;
                const maxPlayers = isOnline ? (server.serverStatus.players ? server.serverStatus.players.max : 0) : 0;
                // const version = isOnline && server.serverStatus.version ? server.serverStatus.version.nameClean : server.version; // 버전 정보는 DB에서 가져온 값 사용

                // 플레이어 수 업데이트 (메인 페이지 전용)
                const playerCountElement = cardElement.querySelector('.player-count span');
                if (playerCountElement) {
                    if (isOnline) {
                        playerCountElement.textContent = `${onlinePlayers} / ${maxPlayers}`;
                    } else {
                        playerCountElement.textContent = `Offline`;
                    }
                }
            }

            fetchServerStatuses();
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
                너에게 맞는 서버를 찾아라! <span class="md-block-hide"></span>
            </h1>

            <p class="hero-subtitle-new">
                저희 CraftConnect 에서는 다양한 장르의 서버들을 제공합니다. <br>
                매일 같은 게임을 하는게 지루하셨다면? 색다른 서버를 찾아 즐겨보세요!
            </p>

            <div class="search-bar-new">
                <div class="search-input-group">
                    <i class='bx bx-search search-icon-new'></i>
                    <input
                            type="text"
                            placeholder="서버 검색하기"
                            class="search-input-field"
                    />
                    <button class="btn-search-new">검색</button>
                </div>
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

                <%-- 🚨 최근 등록된 서버 3개만 반복 --%>
                <c:forEach var="server" items="${serverList}" end="2">
                    <div class="server-card-featured" data-domain="${server.domain}">

                        <div class="card-image-container-featured">

                                <%-- 🚨 이미지 경로: JSP 컴파일 오류 방지를 위한 안전한 Null 체크 --%>
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
                                <c:out value="${server.category}" default="N/A" />
                            </div>
                        </div>

                        <div class="card-content-featured">
                            <h3 class="card-title-featured">${server.name}</h3>

                            <div class="card-divider-featured"></div>

                            <div class="card-meta-featured">
                                <div class="meta-item-featured player-count">
                                    <i class='bx bxs-user-detail'></i>
                                        <%-- 🚨 AJAX가 채울 영역 --%>
                                    <span>로딩 중...</span>
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
<%@ include file="/WEB-INF/views/login-modal.jsp" %>
<script>
    document.addEventListener('DOMContentLoaded', () => {
        const modal = document.getElementById('login-modal');
        // 🚨 이 버튼은 header.jsp 등 메인 페이지에 있어야 합니다.
        const openButton = document.getElementById('main-login-open-btn'); // 메인 페이지의 로그인 버튼 ID로 교체 필요
        const closeButton = document.getElementById('login-close-btn');
        const backdropArea = document.getElementById('modal-backdrop-area');

        // 비밀번호 토글 관련 요소
        const passwordInput = document.getElementById('password-input');
        const togglePasswordBtn = document.getElementById('toggle-password-btn');
        const eyeShow = document.getElementById('eye-show');
        const eyeOff = document.getElementById('eye-off');

        // 모달 닫기 함수
        const closeModal = () => {
            modal.classList.add('hidden');
            document.body.style.overflow = ''; // 스크롤 복구
        };

        // 모달 열기
        if (openButton) {
            openButton.addEventListener('click', (e) => {
                e.preventDefault(); // 기본 링크 동작 방지
                modal.classList.remove('hidden');
                document.body.style.overflow = 'hidden'; // 스크롤 방지
            });
        }

        // 닫기 버튼 클릭 시
        closeButton.addEventListener('click', closeModal);

        // 배경(Backdrop) 클릭 시 닫기
        backdropArea.addEventListener('click', closeModal);

        // ESC 키 눌렀을 때 닫기
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && !modal.classList.contains('hidden')) {
                closeModal();
            }
        });

        // 비밀번호 보이기/숨기기 토글 로직
        if (togglePasswordBtn) {
            togglePasswordBtn.addEventListener('click', (e) => {
                e.preventDefault(); // 폼 제출 방지
                const isPassword = passwordInput.type === 'password';
                passwordInput.type = isPassword ? 'text' : 'password';

                // 아이콘 전환
                eyeShow.classList.toggle('hidden', !isPassword);
                eyeOff.classList.toggle('hidden', isPassword);
            });
        }
    });
</script>
</body>
</html>