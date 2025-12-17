<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<header class="main-header-new">
    <nav class="container main-nav-new">

        <div class="nav-left-new">
            <a href="${pageContext.request.contextPath}/index.jsp" class="logo-new">
                <img src="${pageContext.request.contextPath}/static/images/logo_icon.png" alt="CraftConnect Logo" class="logo-icon">
                CraftConnect
            </a>

            <ul class="nav-links-new">
                <li><a href="${pageContext.request.contextPath}/serverList">Servers</a></li>
                <li><a href="board.page">Community</a></li>
                <li><a href="#">News</a></li>
                <li><a href="#">Support</a></li>
            </ul>
        </div>

        <div class="nav-right-new">

            <%-- 💡 Add Server 링크 --%>
            <a href="${pageContext.request.contextPath}/serverAdd.page" class="btn-add-server-new">Add Server</a>

            <%-- 🚨 JSTL로 로그인 상태 체크 및 렌더링 --%>
            <c:if test="${empty sessionScope.userName}">
                <%-- 💡 로그인 링크에 ID 부여 --%>
                <a href="#" id="main-login-open-btn" class="btn-header-text">Login</a>
            </c:if>

            <c:if test="${not empty sessionScope.userName}">
                <%-- 로그인한 경우 --%>

                <%-- 관리자 메뉴 추가 (Admin Role 체크) --%>
                <c:if test="${sessionScope.userRole == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/user/list" class="btn-header-text admin-link" title="[관리자 메뉴]">
                        Admin
                    </a>
                </c:if>

                <%-- 유저 프로필 및 로그아웃 --%>
                <a href="mypage.page" class="user-profile-new" title="My Page">
                    <img src="https://placehold.co/36x36/9a9a9a/ffffff?text=${fn:toUpperCase(fn:substring(sessionScope.userName, 0, 1))}" alt="User Profile">
                </a>

                <a href="${pageContext.request.contextPath}/user/logout" class="btn-header-text">Logout</a>
            </c:if>
        </div>
    </nav>
</header>


<%-- 1. 모달 HTML 구조 포함 --%>
<%@ include file="/WEB-INF/views/user/login-modal.jsp" %>

<%-- 2. 모달 제어 JavaScript 로직 포함 --%>
<script>
    document.addEventListener('DOMContentLoaded', () => {
        const modal = document.getElementById('login-modal');
        const openButton = document.getElementById('main-login-open-btn');
        const closeButton = document.getElementById('login-close-btn');
        const backdropArea = document.getElementById('modal-backdrop-area');

        // 비밀번호 토글 관련 요소
        const passwordInput = document.getElementById('password-input');
        const togglePasswordBtn = document.getElementById('toggle-password-btn');
        const eyeShow = document.getElementById('eye-show');
        const eyeOff = document.getElementById('eye-off');

        // 모달 닫기 함수
        const closeModal = () => {
            if (modal) {
                modal.classList.add('hidden');
                document.body.style.overflow = ''; // 스크롤 복구
            }
        };

        // 모달 열기
        if (openButton && modal) {
            openButton.addEventListener('click', (e) => {
                e.preventDefault(); // 기본 링크 동작 방지
                modal.classList.remove('hidden');
                document.body.style.overflow = 'hidden'; // 스크롤 방지
            });
        }

        // 닫기 버튼 클릭 시
        if (closeButton) closeButton.addEventListener('click', closeModal);

        // 배경(Backdrop) 클릭 시 닫기
        if (backdropArea) backdropArea.addEventListener('click', closeModal);

        // ESC 키 눌렀을 때 닫기
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal && !modal.classList.contains('hidden')) {
                closeModal();
            }
        });

        // 비밀번호 보이기/숨기기 토글 로직
        if (togglePasswordBtn && passwordInput) {
            togglePasswordBtn.addEventListener('click', (e) => {
                e.preventDefault();
                const isPassword = passwordInput.type === 'password';
                passwordInput.type = isPassword ? 'text' : 'password';

                // 아이콘 전환
                if (eyeShow && eyeOff) {
                    eyeShow.classList.toggle('hidden', !isPassword);
                    eyeOff.classList.toggle('hidden', isPassword);
                }
            });
        }
    });
</script>