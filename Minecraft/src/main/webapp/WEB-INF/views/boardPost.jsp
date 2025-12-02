<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.minecraft.dto.BoardDTO" %>
<%
    BoardDTO board = (BoardDTO) request.getAttribute("board");
    
    Object sessionUserIdObj = session.getAttribute("userId");
    long loginUserId = (sessionUserIdObj != null) ? Long.parseLong(sessionUserIdObj.toString()) : 0;
    String userRole = (String) session.getAttribute("userRole"); // 권한 확인
    
    boolean isEditMode = (board != null); 
    boolean isWriter = (board != null && board.getUserId() == loginUserId);
    
    // [삭제 버튼 표시 조건] 작성자 본인 OR 관리자(ADMIN)
    boolean canDelete = (board != null) && (isWriter || "ADMIN".equals(userRole));
%>

<div class="page-section">
    
    <% if (isEditMode) { %>
    <div id="view-mode-area">
        <div class="board-header" style="border-bottom:none; margin-bottom:10px;">
            <div class="board-title" style="font-size:24px;">
                <span style="font-size:16px; color:#28a745;">[<%= board.getCategory() %>]</span>
                <%= board.getTitle() %>
            </div>
        </div>
        
        <div style="border-bottom:1px solid #ddd; padding-bottom:15px; color:#666; font-size:13px; display:flex; justify-content:space-between;">
            <div>
                <span style="font-weight:bold; color:#333;"><%= board.getWriterName() %></span>
                <span style="margin:0 10px;">|</span>
                <%= board.getCreatedAt() %>
            </div>
            <div>
                조회 <%= board.getViewCount() %> <span style="margin:0 5px;">|</span>
                좋아요 <%= board.getLikeCount() %>
            </div>
        </div>

        <div style="padding: 30px 10px; min-height: 200px; font-size:15px; line-height:1.6; white-space:pre-wrap;"><%= board.getContent() %></div>

        <div style="text-align:center; margin: 30px 0;">
            <button class="btn-like <%= board.isLiked() ? "liked" : "" %>" 
                    onclick="window.postManager.like(<%= board.getBaseBoardId() %>, this)" 
                    style="padding:10px 20px; font-size:14px; border-radius:20px; border:1px solid #ddd; background:#fff; cursor:pointer;">
                ♥ 좋아요 <span id="like-count-display"><%= board.getLikeCount() %></span>
            </button>
        </div>

        <div class="btn-area" style="justify-content: space-between; display:flex;">
            <button class="btn-cancel" onclick="router.goList()">목록</button>
            <div>
                <% if (isWriter) { %>
                    <button class="btn-submit" onclick="window.postManager.showEditForm()" style="background:#333;">수정</button>
                <% } %>
                
                <%-- 삭제 버튼: 관리자 혹은 작성자 --%>
                <% if (canDelete) { %>
                    <button class="btn-cancel" onclick="window.postManager.deletePost(<%= board.getBaseBoardId() %>)" style="color:red; border-color:red;">삭제</button>
                <% } %>
            </div>
        </div>

        <jsp:include page="comment.jsp" />
        
        <script>
            $(document).ready(function() {
                if (window.commentManager) {
                    window.commentManager.init(<%= board.getBaseBoardId() %>);
                }
            });
        </script>
    </div>
    <% } %>

    <div id="write-mode-area" style="<%= isEditMode ? "display:none;" : "" %>">
        <h3 style="margin-bottom:20px;"><%= isEditMode ? "게시글 수정" : "게시글 작성" %></h3>
        <form id="boardForm">
            <input type="hidden" name="id" value="<%= isEditMode ? board.getBaseBoardId() : "" %>">
            <table class="write-table">
                <tr>
                    <th>카테고리</th>
                    <td>
                        <select name="category" class="input-select">
                            <%-- [수정] 관리자(ADMIN)인 경우에만 공지사항 옵션 표시 --%>
                            <% if ("ADMIN".equals(userRole)) { %>
                                <option value="NOTICE">공지사항</option>
                            <% } %>
                            
                            <option value="GREETING">가입 인사</option>
                            <option value="FREE" selected>자유 게시판</option>
                            <option value="PROMOTION">서버 홍보</option>
                            <option value="QNA">질문/답변</option>
                            <option value="SCRIPT">스크립트</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>제목</th>
                    <td>
                        <input type="text" name="title" class="input-text" placeholder="제목을 입력하세요" 
                               value="<%= isEditMode ? board.getTitle() : "" %>">
                    </td>
                </tr>
                <tr>
                    <td colspan="2" class="content-cell">
                        <textarea name="content" class="input-textarea" placeholder="내용을 입력하세요"><%= isEditMode ? board.getContent() : "" %></textarea>
                    </td>
                </tr>
            </table>
            <div class="btn-area">
                <button type="button" class="btn-submit" onclick="window.postManager.save()">저장</button>
                <% if(isEditMode) { %>
                    <button type="button" class="btn-cancel" onclick="window.postManager.cancelEdit()">취소</button>
                <% } else { %>
                    <button type="button" class="btn-cancel" onclick="router.goList()">취소</button>
                <% } %>
            </div>
        </form>
    </div>
</div>

<script>
    if (!window.postManager) {
        window.postManager = {
            save: function() {
                const form = $('#boardForm')[0];
                const formData = new FormData(form);
                if(!formData.get('title').trim()) { alert('제목을 입력하세요'); return; }
                if(!formData.get('content').trim()) { alert('내용을 입력하세요'); return; }

                $.ajax({
                    url: contextPath + '/board/save',
                    type: 'POST',
                    data: $(form).serialize(),
                    dataType: 'json',
                    success: function(res) {
                        if(res.status === 'success') {
                            alert(res.message);
                            router.goList();
                        } else { alert(res.message); }
                    },
                    error: function() { alert('오류 발생'); }
                });
            },
            deletePost: function(id) {
                if(!confirm('정말 삭제하시겠습니까?')) return;
                $.ajax({
                    url: contextPath + '/board/delete',
                    type: 'POST',
                    data: { id: id },
                    dataType: 'json',
                    success: function(res) {
                        if(res.status === 'success') { alert('삭제되었습니다.'); router.goList(); } 
                        else { alert(res.message); }
                    }
                });
            },
            like: function(id, btnElement) {
                const $btn = $(btnElement);
                const $countSpan = $btn.find('#like-count-display');
                $.ajax({
                    url: contextPath + '/board/like',
                    type: 'POST',
                    data: { id: id },
                    dataType: 'json',
                    success: function(res) {
                        if(res.status === 'success') {
                            let currentCount = parseInt($countSpan.text());
                            if (res.liked) {
                                $btn.addClass('liked');
                                $countSpan.text(currentCount + 1);
                            } else {
                                $btn.removeClass('liked');
                                $countSpan.text(currentCount > 0 ? currentCount - 1 : 0);
                            }
                        } else { alert(res.message); }
                    }
                });
            },
            showEditForm: function() { $('#view-mode-area').hide(); $('#write-mode-area').show(); },
            cancelEdit: function() { $('#write-mode-area').hide(); $('#view-mode-area').show(); }
        };
    }

    $(document).ready(function() {
        <% if (isEditMode) { %>
            $('select[name="category"]').val('<%= board.getCategory() %>');
        <% } %>
    });
</script>