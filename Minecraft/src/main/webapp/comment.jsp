<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 
    [파일명: /WEB-INF/views/comment.jsp] 
    설명: 게시글 상세 페이지에 include 되어 동작하는 댓글 모듈
--%>

<style>
    .comment-section { background-color: #f9f9f9; padding: 20px; border-top: 1px solid #eee; margin-top: 30px; }
    .comment-count-box { font-size: 14px; font-weight: bold; margin-bottom: 15px; color: #333; }
    
    /* 댓글 리스트 */
    .comment-list { list-style: none; margin-bottom: 20px; border-top: 1px solid #ddd; padding-left: 0; }
    .comment-item { padding: 15px 10px; border-bottom: 1px solid #ddd; background: #fff; }
    
    .cmt-meta { margin-bottom: 8px; font-size: 13px; display: flex; justify-content: space-between; align-items: center; }
    .cmt-writer { font-weight: bold; color: #333; }
    .cmt-date { color: #888; font-size: 12px; margin-left: 10px; }
    
    .cmt-content { font-size: 14px; color: #555; line-height: 1.5; white-space: pre-wrap; margin-bottom: 8px; }
    
    /* 좋아요 및 삭제 버튼 영역 */
    .cmt-actions { display: flex; gap: 15px; align-items: center; font-size: 12px; }
    
    /* 좋아요 버튼 스타일 */
    .btn-like { cursor: pointer; border: 1px solid #ddd; background: #fff; padding: 4px 10px; border-radius: 15px; color: #666; transition: all 0.2s; }
    .btn-like:hover { background: #f0f0f0; }
    .btn-like.liked { border-color: #ff4e50; color: #ff4e50; background: #fff0f0; }
    .btn-like i { margin-right: 3px; }
    
    .btn-cmt-del { color: #999; cursor: pointer; background: none; border: none; text-decoration: underline; font-size: 11px; }

    /* 작성 폼 */
    .comment-form { display: flex; gap: 10px; background: #fff; padding: 15px; border: 1px solid #ddd; border-radius: 4px; }
    .comment-input { flex-grow: 1; border: 1px solid #e5e5e5; padding: 10px; resize: none; height: 60px; font-size: 13px; outline: none; }
    .comment-input:focus { border-color: #28a745; }
    .btn-cmt-submit { width: 80px; background-color: #28a745; color: #fff; border: none; font-weight: bold; cursor: pointer; border-radius: 4px; }
    .btn-cmt-submit:hover { background-color: #218838; }
</style>

<div class="comment-section">
    <div class="comment-count-box">
        댓글 <span id="cmt-total-count" style="color:#28a745">0</span>개
    </div>
    
    <ul class="comment-list" id="comment-list-box">
        </ul>

    <div class="comment-form">
        <textarea id="cmt-input" class="comment-input" placeholder="댓글을 남겨보세요."></textarea>
        <button type="button" class="btn-cmt-submit" onclick="commentManager.add()">등록</button>
    </div>
</div>

<script>
    /**
     * [댓글 관리자 객체]
     * 외부(boardPost.jsp)에서 commentManager.init(boardId)를 호출하여 사용
     */
    const commentManager = {
        boardId: null,      // 현재 게시글 ID
        comments: [],       // 댓글 데이터 저장소

        // 1. 초기화 및 불러오기
        init: function(id) {
            this.boardId = id;
            this.loadComments();
        },

        // 2. 댓글 목록 조회 (AJAX Mock)
        loadComments: function() {
        	// 자바스크립트의 일반적인 문자열 연결 방식으로 변경
        	console.log('[AJAX] 게시글 ' + this.boardId + '번의 댓글을 불러옵니다.');

            // [TODO: 실제 AJAX 호출]
            // $.ajax({ url: '/comment/list.do', data: { bid: this.boardId }, success: (data) => this.render(data) });

            // [Mock Data]
            const mockData = [
                { cid: 10, writer: "유저1", content: "댓글 분리하니 깔끔하네요.", date: "2025.11.27 15:00", likes: 5, isLiked: false },
                { cid: 11, writer: "유저2", content: "좋아요 기능 테스트!", date: "2025.11.27 15:10", likes: 0, isLiked: true }
            ];
            
            // 데이터가 없으면 빈 배열
            this.comments = mockData || []; 
            this.render();
        },

        // 3. 화면 렌더링 (새로고침 없음)
        render: function() {
            const listEl = $('#comment-list-box');
            $('#cmt-total-count').text(this.comments.length);
            listEl.empty();

            if (this.comments.length === 0) {
                listEl.html('<li class="comment-item" style="text-align:center; color:#999; padding:30px;">첫 번째 댓글을 남겨보세요!</li>');
                return;
            }

            let html = '';
            this.comments.forEach((cmt, index) => {
                // 좋아요 상태에 따른 클래스 및 아이콘 설정
                const likeClass = cmt.isLiked ? 'liked' : '';
                const likeIcon = cmt.isLiked ? '♥' : '♡'; // 폰트어썸 사용 시 <i class="fa fa-heart"></i>

                html += `
                    <li class="comment-item">
                        <div class="cmt-meta">
                            <div>
                                <span class="cmt-writer">${cmt.writer}</span>
                                <span class="cmt-date">${cmt.date}</span>
                            </div>
                            <button class="btn-cmt-del" onclick="commentManager.remove(${cmt.cid})">삭제</button>
                        </div>
                        <div class="cmt-content">${cmt.content}</div>
                        
                        <div class="cmt-actions">
                            <button class="btn-like ${likeClass}" onclick="commentManager.toggleLike(${index})">
                                ${likeIcon} 좋아요 <span id="like-cnt-${index}">${cmt.likes}</span>
                            </button>
                        </div>
                    </li>
                `;
            });
            listEl.html(html);
        },

        // 4. 댓글 등록 (AJAX)
        add: function() {
            const content = $('#cmt-input').val();
            if (!content.trim()) { alert("내용을 입력해주세요."); return; }

            // [TODO: 실제 AJAX] $.ajax({ url: '/comment/add.do', ... })

            // [Mock] 즉시 리스트에 추가
            const newCmt = {
                cid: Date.now(),
                writer: "나(Current)",
                content: content,
                date: "방금 전",
                likes: 0,
                isLiked: false
            };
            
            this.comments.push(newCmt);
            $('#cmt-input').val(''); // 입력창 초기화
            this.render(); // 다시 그리기 (새로고침 X)
        },

        // 5. 댓글 삭제 (AJAX)
        remove: function(cid) {
            if (!confirm("정말 삭제하시겠습니까?")) return;

            // [TODO: 실제 AJAX] $.ajax({ url: '/comment/del.do', ... })

            // [Mock] 배열에서 제거
            this.comments = this.comments.filter(c => c.cid !== cid);
            this.render();
        },

        // 6. 좋아요 토글 (AJAX - 부분 갱신)
        toggleLike: function(index) {
            const cmt = this.comments[index];
            
            // [TODO: 실제 AJAX] $.ajax({ url: '/comment/like.do', ... })

            // [Mock] 상태 변경
            if (cmt.isLiked) {
                cmt.likes--;     // 취소
                cmt.isLiked = false;
            } else {
                cmt.likes++;     // 좋아요
                cmt.isLiked = true;
            }

            // 전체 렌더링 대신 해당 버튼만 갱신하거나, 편의상 render() 호출
            // 여기선 데이터가 바뀌었으므로 render()로 UI 동기화
            this.render();
        }
    };
</script>