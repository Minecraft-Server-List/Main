<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // [SERVER-SIDE] ID 파라미터로 모드 판별
    String id = request.getParameter("id");
    boolean isViewMode = (id != null && !id.isEmpty());
    
    // Mock Data (상세 조회용 가짜 데이터)
    String title = "";
    String content = "";
    String writer = "작성자";
    String date = "2025.11.28";
    
    if(isViewMode) {
        title = "게시글 " + id + "번 상세 내용입니다.";
        content = "AJAX로 로드된 내용입니다.<br>수정 버튼을 누르면 이 내용이 폼에 채워집니다.";
        writer = "유저" + id;
    }
%>

<style>
    /* 기본적으로 숨김 처리 (중요!) */
    .section-mode { 
        display: none !important; 
    }
    
    /* active 클래스가 붙으면 보임 처리 */
    .section-mode.active { 
        display: block !important; 
        animation: fadeIn 0.3s ease-in-out;
    }

    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(5px); }
        to { opacity: 1; transform: translateY(0); }
    }
</style>

<% if(isViewMode) { %>
<div id="mode-view" class="section-mode active">
    <div class="board-header" style="border-bottom:none; margin-bottom:0;">
        <div class="board-title"><%= title %></div>
    </div>
    <div style="border-bottom:2px solid #333; padding-bottom:10px; margin-bottom:20px; color:#666; font-size:13px;">
        <span style="margin-right:15px; font-weight:bold; color:#333;"><%= writer %></span>
        <span><%= date %></span>
    </div>
    
    <div class="view-content" style="min-height:200px; border-bottom:1px solid #eee; margin-bottom:30px; padding:10px;">
        <%= content %>
    </div>

    <div class="btn-area" style="text-align: right; margin-bottom: 30px;">
        <button type="button" class="btn-cancel link-list">목록</button>
        <button type="button" class="btn-submit" onclick="toggleEditMode()">수정</button>
    </div>

    <jsp:include page="/comment.jsp" />
    
    <script>
        $(document).ready(function(){
            // 댓글 매니저가 로드되어 있다면 초기화 실행
            if(typeof commentManager !== 'undefined') {
                commentManager.init('<%= id %>'); 
            }
        });
    </script>
</div>
<% } %>

<div id="mode-form" class="section-mode <%= !isViewMode ? "active" : "" %>">
    <div class="board-header">
        <div class="board-title"><%= isViewMode ? "게시글 수정" : "카페 글쓰기" %></div>
    </div>

    <form id="boardForm">
        <input type="hidden" name="id" value="<%= isViewMode ? id : "" %>">
        
        <table class="write-table">
            <colgroup><col style="width: 15%;"><col style="width: 85%;"></colgroup>
            <tbody>
                <tr>
                    <th>카테고리</th>
                    <td>
                        <select name="category" class="input-select">
                            <option value="free">자유 게시판</option>
                            <option value="promo">서버 홍보</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>제목</th>
                    <td>
                        <input type="text" name="title" class="input-text" value="<%= title %>" placeholder="제목을 입력하세요">
                    </td>
                </tr>
                <tr>
                    <td colspan="2" class="content-cell">
                        <textarea name="content" class="input-textarea" placeholder="내용을 입력하세요"><%= content.replace("<br>", "\n") %></textarea>
                    </td>
                </tr>
            </tbody>
        </table>
        
        <div class="btn-area">
            <button type="button" class="btn-cancel" onclick="cancelWrite()">취소</button>
            <button type="button" class="btn-submit" onclick="submitPost()">저장</button>
        </div>
    </form>
</div>

<script>
    // [수정] 버튼 클릭 시 -> 조회 숨김, 폼 보임
    function toggleEditMode() {
        $('#mode-view').removeClass('active');
        $('#mode-form').addClass('active');
    }

    // [취소] 버튼 클릭 시
    function cancelWrite() {
        const id = $('input[name="id"]').val();
        
        if(id) {
            // 수정 중 취소 -> 폼 숨김, 조회 보임 (원상복구)
            $('#mode-form').removeClass('active');
            $('#mode-view').addClass('active');
        } else {
            // 새 글 작성 중 취소 -> 목록으로 이동
            if(typeof router !== 'undefined') {
                router.goList();
            }
        }
    }

    // [저장] 버튼 클릭 시
    function submitPost() {
        const title = $('input[name="title"]').val();
        if(!title) { alert("제목을 입력하세요"); return; }
        
        // [AJAX] 실제로는 여기서 $.ajax로 서버 전송
        const id = $('input[name="id"]').val();
        const msg = id ? "수정되었습니다." : "등록되었습니다.";
        
        alert(msg);
        
        // 저장 후 목록으로 이동
        if(typeof router !== 'undefined') {
            router.goList();
        }
    }
</script>