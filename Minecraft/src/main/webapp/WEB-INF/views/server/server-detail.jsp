<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8" />
    <title>${server.name} 서버 상세 정보</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/footer.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/server-detail.css">

    <script>
        function handleCopy() {
            // 서버 도메인(${server.domain})을 사용
            const address = document.getElementById('serverAddress').innerText;
            navigator.clipboard.writeText(address).then(() => {
                const button = document.getElementById('copyButton');
                button.classList.add('copied');
                setTimeout(() => button.classList.remove('copied'), 2000);
            }).catch(err => {
                console.error('클립보드 복사 실패:', err);
            });
        }
    </script>
</head>

<body class="detail-body">
<%@ include file="/WEB-INF/views/header.jsp" %>

<main class="detail-main-container">

    <div class="server-banner-section">
        <div class="hero-bg-ts"
             style="background-image: url('<c:url value="/upload/server_images/${server.serverImage.fileName}" />');">
        </div>
        <div class="hero-gradient-overlay-ts"></div>

        <div class="back-button-ts">
            <button onclick="history.back()" class="btn-back-ts">
                <i class='bx bx-chevron-left'></i>
                <span>목록으로</span>
            </button>
        </div>

        <div class="server-info-bottom-ts">
            <div class="container server-info-inner-ts">

                <div class="server-icon-ts">
                    <img src="<c:url value="/upload/server_images/${server.serverImage.fileName}" />" alt="Server Icon" class="rounded-icon-ts">
                </div>

                <div class="server-text-content-ts">
                    <div class="status-name-row-ts">
                        <h1 class="server-name-ts">${server.name}</h1>

                        <%-- DB status 필드 사용 --%>
                        <span class="status-badge-ts ${server.status eq 'ACTIVE' ? 'online' : 'offline'}">
                            <div class="status-dot-ts"></div>
                            <c:out value="${server.status eq 'ACTIVE' ? 'ONLINE' : 'OFFLINE'}" />
                        </span>
                    </div>

                    <p class="server-description-ts">
                        ${server.description}
                    </p>

                    <div class="server-tags-ts">
                        <span class="tag-ts">${server.category}</span>
                        <span class="tag-ts">Survival</span>
                        <span class="tag-ts">Economy</span>
                        <span class="tag-ts">PvP</span>
                        <span class="tag-ts">Custom</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="container main-content-grid-ts">

        <div class="content-left-ts">

            <div class="tabs-container-ts">
                <div class="tabs-nav-ts">
                    <button class="tab-item-ts active">개요</button>
                    <button class="tab-item-ts">게임 모드</button>
                    <button class="tab-item-ts">스크린샷</button>
                    <button class="tab-item-ts">리뷰</button>
                </div>

                <div class="tabs-content-ts">
                    <div class="overview-section-ts">
                        <div class="detail-block-ts">
                            <h2 class="section-title-ts">서버 소개</h2>
                            <p class="text-regular-ts">${server.description}</p>

                            <h3 class="feature-title-ts">주요 특징</h3>
                            <ul class="feature-list-ts">
                                <c:forEach var="feature" items="${['안정적인 서버 운영 (99.9% 가동률)', '정기적인 업데이트와 새로운 콘텐츠', '친절한 커뮤니티와 활발한 이벤트', '공정한 게임 환경 (안티치트 시스템)', '빠른 고객 지원']}">
                                    <li class="feature-item-ts">
                                        <div class="dot-ts"></div>
                                            ${feature}
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>

                        <div class="detail-block-ts">
                            <h2 class="section-title-ts">서버 규칙</h2>
                            <div class="rules-list-ts">
                                <c:forEach var="rule" items="${rules}">
                                    <div class="rule-item-ts">
                                        <i class='bx bx-check-circle'></i>
                                        <span class="rule-text-ts">${rule}</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="detail-block-ts">
                            <h2 class="section-title-ts">운영진</h2>
                            <div class="staff-grid-ts">
                                <c:forEach var="staff" items="${staffMembers}">
                                    <div class="staff-member-ts">
                                        <img src="https://api.dicebear.com/9.x/avataaars/svg?seed=Staff${staff.id}" alt="${staff.name}" class="staff-avatar-ts" />
                                        <div>
                                            <p class="staff-name-ts">Admin_Steve</p>
                                            <p class="staff-role-ts">서버 관리자</p>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="sidebar-right-ts">

            <div class="info-card-ts">
                <h3 class="card-header-ts">서버 정보</h3>

                <div class="info-group-ts">
                    <label class="info-label-ts">서버 주소</label>
                    <div class="address-input-group-ts">
                        <div id="serverAddress" class="address-text-ts">
                            <%-- DB domain 필드 사용 --%>
                            ${server.domain}
                        </div>
                        <button id="copyButton" onclick="handleCopy()" class="btn-copy-ts">
                            <i class='bx bx-copy'></i>
                            <i class='bx bx-check-circle'></i>
                        </button>
                    </div>
                </div>

                <div class="info-group-ts player-progress-group">
                    <div class="progress-label-row-ts">
                        <span class="progress-label-ts">플레이어</span>
                        <%-- DB onlinePlayers, maxPlayers 필드 사용 --%>
                        <span class="progress-count-ts">${server.onlinePlayers} / ${server.maxPlayers}</span>
                    </div>
                    <div class="progress-bar-wrap-ts">
                        <%-- DB 값 기반 비율 계산 --%>
                        <c:set var="playerRatio" value="${(server.onlinePlayers / server.maxPlayers) * 100}" />
                        <div class="progress-bar-ts" style="width: ${playerRatio}%;"></div>
                    </div>
                </div>

                <div class="meta-data-ts">
                    <div class="meta-item-ts">
                        <span class="meta-label-ts">버전</span>
                        <%-- DB version 필드 사용 --%>
                        <span class="meta-value-ts">${server.version}</span>
                    </div>
                    <div class="meta-item-ts">
                        <span class="meta-label-ts">카테고리</span>
                        <span class="meta-value-ts">${server.category}</span>
                    </div>
                    <div class="meta-item-ts">
                        <span class="meta-label-ts">위치</span>
                        <span class="meta-value-ts">대한민국 (서울)</span>
                    </div>
                    <div class="meta-item-ts">
                        <span class="meta-label-ts">가동률</span>
                        <span class="meta-value-ts status-online-text">99.9%</span>
                    </div>
                    <div class="meta-item-ts">
                        <span class="meta-label-ts">오픈일</span>
                        <span class="meta-value-ts">2018년 3월</span>
                    </div>
                    <div class="meta-item-ts">
                        <span class="meta-label-ts">총 투표</span>
                        <span class="meta-value-ts">12,847</span>
                    </div>
                </div>

                <div class="action-buttons-ts">
                    <button class="btn-action-ts btn-vote-ts">
                        <i class='bx bxs-heart'></i>
                        투표하기
                    </button>
                    <%-- DB domain 필드 사용 --%>
                    <a href="<c:url value="http://${server.domain}" />" target="_blank" class="btn-action-ts btn-visit-ts">
                        <i class='bx bx-globe'></i>
                        웹사이트 방문
                    </a>
                    <button class="btn-action-ts btn-discord-ts">
                        <i class='bx bxl-discord-alt'></i>
                        디스코드 참여
                    </button>
                </div>

            </div>
        </div>
    </div>

    <div class="container similar-servers-section-ts">
        <h2 class="section-title-similar-ts">비슷한 서버</h2>
        <div class="similar-grid-ts">
            <c:forEach var="i" begin="1" end="3">
                <div class="similar-card-ts">
                    <div class="similar-image-wrap-ts">
                        <img src="https://picsum.photos/seed/${i+100}/400/300" class="similar-image-ts" alt="Server" />
                    </div>
                    <div class="similar-content-ts">
                        <h3 class="similar-title-ts">Server Name ${i}</h3>
                        <div class="similar-meta-ts">
                            <div class="meta-item-small-ts">
                                <i class='bx bxs-user-detail'></i>
                                <span>1,${i}00</span>
                            </div>
                            <div class="meta-item-small-ts heart-count">
                                <i class='bx bxs-heart'></i>
                                <span>${5 + i},500</span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>