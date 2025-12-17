<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>
    window.currentSessionUserId = <%= session.getAttribute("userId") != null ? session.getAttribute("userId") : 0 %>;
</script>

<div class="comment-wrap">
    <div style="font-weight:bold; margin-bottom:15px;">
        댓글 <span id="cmt-total-count">0</span>개
    </div>
    
    <div id="comment-list-box"></div>

    <div class="comment-input-area">
        <textarea id="cmt-input" placeholder="댓글을 입력하세요."></textarea>
        <button type="button" onclick="window.commentManager.add()">등록</button>
    </div>
</div>

<script>
    if (!window.commentManager) {
        window.commentManager = {
            boardId: null,
            init: function(id) {
                this.boardId = id;
                this.loadComments();
            },
            loadComments: function() {
                const that = this;
                $.ajax({
                    url: contextPath + '/comment/list',
                    type: 'GET',
                    data: { bid: that.boardId },
                    dataType: 'json',
                    success: function(data) { that.render(data); }
                });
            },
            render: function(comments) {
                const listEl = $('#comment-list-box');
                $('#cmt-total-count').text(comments.length);
                listEl.empty();

                if (!comments || comments.length === 0) {
                    listEl.html('<div style="padding:20px; text-align:center; color:#999;">작성된 댓글이 없습니다.</div>');
                    return;
                }

                let html = '';
                comments.forEach(c => {
                    const likeClass = c.isLiked ? 'liked' : '';
                    
                    // [삭제 버튼 조건] 본인 OR 관리자
                    let deleteBtn = '';
                    if (window.currentSessionUserId != 0 && 
                       (window.currentSessionUserId == c.writerId || c.currentUserRole === 'ADMIN')) {
                        deleteBtn = `<button onclick="window.commentManager.remove(\${c.cid})" style="border:none; background:none; cursor:pointer; color:#999; font-size:11px;">삭제</button>`;
                    }

                    html += `
                        <div class="cmt-item">
                            <div class="cmt-header">
                                <div>
                                    <span class="cmt-writer">\${c.writer}</span>
                                    <span class="cmt-date">\${c.date}</span>
                                </div>
                                \${deleteBtn}
                            </div>
                            <div class="cmt-content">\${c.content}</div>
                            
                            <div class="cmt-actions">
                                <button class="btn-cmt-like \${likeClass}" onclick="window.commentManager.toggleLike(\${c.cid}, this)">
                                    ♥ 좋아요 <span>\${c.likes}</span>
                                </button>
                            </div>
                        </div>
                    `;
                });
                listEl.html(html);
            },
            add: function() {
                const content = $('#cmt-input').val();
                if (!content.trim()) { alert("내용을 입력하세요"); return; }
                const that = this;
                $.ajax({
                    url: contextPath + '/comment/add',
                    type: 'POST',
                    data: { base_board_id: that.boardId, content: content },
                    dataType: 'json',
                    success: function(res) {
                        if(res.status === 'success') { $('#cmt-input').val(''); that.loadComments(); } 
                        else { alert(res.message || "로그인 필요"); }
                    }
                });
            },
            remove: function(cid) {
                if (!confirm("삭제하시겠습니까?")) return;
                const that = this;
                $.ajax({
                    url: contextPath + '/comment/delete',
                    type: 'POST',
                    data: { coment_id: cid },
                    dataType: 'json',
                    success: function(res) {
                        if(res.status === 'success') { that.loadComments(); } 
                        else { alert(res.message || "삭제 실패"); }
                    }
                });
            },
            toggleLike: function(cid, btnElement) {
                $.ajax({
                    url: contextPath + '/comment/like',
                    type: 'POST',
                    data: { id: cid },
                    dataType: 'json',
                    success: function(res) {
                        if(res.status === 'success') { commentManager.loadComments(); } 
                        else { alert(res.message); }
                    }
                });
            }
        };
    }
</script>