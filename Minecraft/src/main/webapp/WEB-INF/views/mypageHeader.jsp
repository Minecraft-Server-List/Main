<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 세션에서 현재 활성화된 탭 정보 가져오기 (없으면 기본값 'edit')
    String activeTab = (String) session.getAttribute("activeMypageTab");
    if (activeTab == null) {
        activeTab = "edit";
    }
%>

<%-- 마이페이지 제목 영역 --%>
<div class="mypage-title">
    <h2>마이페이지</h2>
</div>

<%-- 탭 네비게이션 영역 --%>
<div class="mypage-nav-container">
    <div class="mypage-nav-links">
        <%-- 탭 1: 개인정보 수정 --%>
        <a href="#" data-tab="edit" onclick="loadMypageContent('edit', this); return false;"
           class="<%= "edit".equals(activeTab) ? "active" : "" %>">
            개인정보 수정
        </a>
        
        <%-- 탭 2: 친구목록 --%>
        <a href="#" data-tab="boardCreate" onclick="loadMypageContent('boardCreate', this); return false;"
           class="<%= "boardCreate".equals(activeTab) ? "active" : "" %>">
            친구목록
        </a>
        
        <%-- 탭 3: 본인 작성 게시글 목록 (BoardServlet 연결) --%>
        <a href="#" data-tab="myPostList" onclick="loadMypageContent('myPostList', this); return false;"
           class="<%= "myPostList".equals(activeTab) ? "active" : "" %>">
            작성 게시글 목록
        </a>
        
        <%-- 탭 4: 본인 작성 코멘트 목록 (ComentServlet 연결) --%>
        <a href="#" data-tab="myCommentList" onclick="loadMypageContent('myCommentList', this); return false;"
           class="<%= "myCommentList".equals(activeTab) ? "active" : "" %>">
            작성 코멘트 목록
        </a>
    </div>
</div>

<script>
/**
 * AJAX를 사용하여 마이페이지 본문 내용을 로드하고 탭 상태를 업데이트하는 함수
 * @param {string} tabName - 선택된 탭 이름
 * @param {HTMLElement} clickedElement - 클릭된 <a> 태그 요소
 */
function loadMypageContent(tabName, clickedElement) {
    const contentArea = document.querySelector('.mypage-content-area');
    const navLinks = document.querySelectorAll('.mypage-nav-links a');
    
    // 1. 활성 탭 스타일 변경 (UI 업데이트)
    navLinks.forEach(link => link.classList.remove('active'));
    if(clickedElement) {
        clickedElement.classList.add('active');
    }

    // 2. 요청할 URL 설정
    let contentUrl = '';
    const contextPath = '<%= request.getContextPath() %>';
    const userEmail = '<%= session.getAttribute("userEmail") %>'; 

    if (tabName === 'edit') {
        // 개인정보 수정 (기존)
        contentUrl = contextPath + '/searchUser.do?email=' + userEmail;
    } else if (tabName === 'boardCreate') {
        // 게시판 생성 (기존)
        contentUrl = contextPath + '/boardCreate.do';
        
    } else if (tabName === 'myPostList') {
        // [수정] 작성 게시글 목록 -> BoardServlet의 /myList 경로 호출
        contentUrl = contextPath + '/board/myList';
        
    } else if (tabName === 'myCommentList') {
        // [수정] 작성 코멘트 목록 -> ComentServlet의 /myList 경로 호출
        contentUrl = contextPath + '/comment/myList';
        
    } else {
        contentArea.innerHTML = '<p style="padding: 30px;">잘못된 탭 요청입니다.</p>';
        return;
    }

    // 로딩 스피너 표시
    contentArea.innerHTML = '<div class="loading-spinner" style="display:block; text-align:center; padding:50px; color:#666;">로딩 중...</div>';

    // 3. AJAX 호출 (Fetch API)
    fetch(contentUrl, { 
        method: 'GET',
        headers: {
            // 서블릿에서 AJAX 요청임을 식별할 수 있도록 헤더 추가
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('네트워크 응답 오류: ' + response.status);
        }
        return response.text(); // HTML 조각을 텍스트로 받음
    })
    .then(html => {
        // 4. 받아온 HTML을 본문 영역에 삽입
        contentArea.innerHTML = html;
        
        // 5. 현재 탭 상태를 서버 세션에 저장 (새로고침 시 유지용)
        fetch(contextPath + '/updateTabSession.do?tab=' + tabName);
    })
    .catch(error => {
        console.error('AJAX 로드 실패:', error);
        contentArea.innerHTML = '<p style="padding: 30px; text-align:center; color: red;">데이터를 불러오는 데 실패했습니다.</p>';
    });
}

// 페이지 로드 시 초기 탭 실행
window.onload = function() {
    const defaultTabName = '<%= activeTab %>';
    // data-tab 속성을 이용해 초기 활성화될 탭 요소 찾기
    const activeLink = document.querySelector('.mypage-nav-links a[data-tab="' + defaultTabName + '"]');

    if (activeLink) {
        loadMypageContent(defaultTabName, activeLink);
    }
}
</script>