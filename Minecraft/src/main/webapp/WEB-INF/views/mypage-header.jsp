<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String activeTab = (String) session.getAttribute("activeMypageTab");
    if (activeTab == null) {
        activeTab = "edit";
    }
%>
<style>
/* 마이페이지 내비게이션 전용 스타일 */
.mypage-nav-container {
    background-color: #ffffff;
    border-bottom: 1px solid #eaeaea;
    padding: 0 0;
    margin-bottom: 30px;
}

.mypage-nav-links {
    display: flex;
    justify-content: flex-start;
    max-width: 1100px;
    margin: 0 auto;
    padding: 0 20px;
}

.mypage-nav-links a {
    display: block;
    padding: 15px 25px;
    font-size: 16px;
    color: #555;
    font-weight: 500;
    text-decoration: none;
    transition: color 0.2s, border-bottom 0.2s;
    border-bottom: 3px solid transparent;
}

.mypage-nav-links a:hover {
    color: #000;
    border-bottom: 3px solid #e0e0e0;
}

.mypage-nav-links a.active {
    color: #28a745; /* Green color for active tab */
    font-weight: 600;
    border-bottom: 3px solid #28a745;
}

/* 추가 스타일: 마이페이지 제목 */
.mypage-title {
    max-width: 1100px;
    margin: 0 auto;
    padding: 0 20px;
    margin-bottom: 20px;
    padding-top: 20px;
}
.mypage-title h2 {
    font-size: 32px;
    font-weight: 700;
    color: #222;
    text-align: left;
    margin-bottom: 0;
}
</style>

<div class="mypage-title">
    <h2>마이페이지</h2>
</div>
<div class="mypage-nav-container">
    <div class="mypage-nav-links">
        <%-- 탭 1: 개인정보 수정 --%>
        <a href="#" data-tab="edit" onclick="loadMypageContent('edit', this); return false;"
           class="<%= "edit".equals(activeTab) ? "active" : "" %>">
            개인정보 수정
        </a>
        
        <%-- 탭 2: 게시판 생성 --%>
        <a href="#" data-tab="boardCreate" onclick="loadMypageContent('boardCreate', this); return false;"
           class="<%= "boardCreate".equals(activeTab) ? "active" : "" %>">
            게시판 생성
        </a>
        
        <%-- 탭 3: 본인 작성 게시글 목록 --%>
        <a href="#" data-tab="myPostList" onclick="loadMypageContent('myPostList', this); return false;"
           class="<%= "myPostList".equals(activeTab) ? "active" : "" %>">
            작성 게시글 목록
        </a>
        
        <%-- 탭 4: 본인 작성 코멘트 목록 --%>
        <a href="#" data-tab="myCommentList" onclick="loadMypageContent('myCommentList', this); return false;"
           class="<%= "myCommentList".equals(activeTab) ? "active" : "" %>">
            작성 코멘트 목록
        </a>
    </div>
</div>

<script>
/**
 * AJAX를 사용하여 마이페이지 본문 내용을 로드하고 탭 상태를 업데이트하는 함수
 * @param {string} tabName - 선택된 탭 이름 (예: 'edit', 'myPostList')
 * @param {HTMLElement} clickedElement - 클릭된 <a> 태그 요소
 */
function loadMypageContent(tabName, clickedElement) {
    const contentArea = document.querySelector('.mypage-content-area');
    const navLinks = document.querySelectorAll('.mypage-nav-links a');
    
    // 1. 활성 탭 스타일 변경
    navLinks.forEach(link => link.classList.remove('active'));
    clickedElement.classList.add('active');

    let contentUrl = '';
    const contextPath = '<%= request.getContextPath() %>';
    const userEmail = '<%= session.getAttribute("userEmail") %>'; 

    // 2. 탭 이름에 따라 요청할 서블릿(.do) 경로 결정
    if (tabName === 'edit') {
        contentUrl = contextPath + '/searchUser.do?email=' + userEmail;
    } else if (tabName === 'boardCreate') {
        contentUrl = contextPath + '/boardCreate.do';
    } else if (tabName === 'myPostList') {
        contentUrl = contextPath + '/myPostList.do';
    } else if (tabName === 'myCommentList') {
        contentUrl = contextPath + '/myCommentList.do';
    } else {
        contentArea.innerHTML = '<p style="padding: 30px;">잘못된 탭 요청입니다.</p>';
        return;
    }

    contentArea.innerHTML = '<p style="padding: 30px; text-align: center;">로딩 중...</p>'; // 로딩 표시

    // 3. AJAX 호출 (Fetch API 사용)
    fetch(contentUrl, { 
        method: 'GET',
        // 🚨 중요: SearchUserServlet이 AJAX 요청임을 인식하도록 헤더 추가 🚨
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('네트워크 응답 오류: ' + response.status);
        }
        return response.text(); // 서버에서 HTML 조각(Fragment)을 텍스트로 받음
    })
    .then(html => {
        // 4. 본문 영역에 받은 HTML 삽입
        contentArea.innerHTML = html;
        
        // 5. 서버에 세션 상태 업데이트 요청 (비동기 처리)
        fetch(contextPath + '/updateTabSession.do?tab=' + tabName);
    })
    .catch(error => {
        console.error('AJAX 로드 실패:', error);
        contentArea.innerHTML = '<p style="padding: 30px; color: red;">콘텐츠를 로드하는 데 실패했습니다.</p>';
    });
}
// 페이지 로드 후 로직: 세션에 설정된 탭의 콘텐츠를 AJAX로 로드합니다.
window.onload = function() {
    const defaultTabName = '<%= activeTab %>';
    const activeLink = document.querySelector('.mypage-nav-links a.active');

    if (activeLink) {
        // activeTab이 세션에서 로드된 값이면, 해당 탭을 JavaScript로 로드하여 콘텐츠를 표시합니다.
        loadMypageContent(defaultTabName, activeLink);
    }
}
</script>