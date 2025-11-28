<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>커뮤니티 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>

    <jsp:include page="/WEB-INF/views/header.jsp" />

    <main class="container">
        <div class="board-layout">
            
            <jsp:include page="/boardSidebar.jsp" />

            <section class="board-main">
                
                <div id="loading" class="loading-spinner">
                    <p>페이지를 불러오는 중입니다...</p>
                </div>

                <div id="dynamic-content"></div>

            </section>
        </div>
    </main>

    <jsp:include page="/WEB-INF/views/footer.jsp" />

    <script>
        const router = {
            target: $('#dynamic-content'),
            loader: $('#loading'),

            // JSP 조각 불러오기
            load: function(url) {
                this.target.empty(); 
                this.loader.show();
                
                // .load(url, params, callback)
                this.target.load(url, function(response, status, xhr) {
                    router.loader.hide();
                    if (status == "error") {
                        router.target.html("<div style='text-align:center; padding:50px;'>페이지 로드 실패: " + xhr.status + "</div>");
                    }
                });
            },

            // 1. 목록으로 이동
            goList: function() {
                this.load("boardList.jsp");
            },

            // 2. 상세/작성으로 이동 (ID가 있으면 상세, 없으면 작성)
            goPost: function(id) {
                const url = id ? "boardPost.jsp?id=" + id : "boardPost.jsp";
                this.load(url);
            }
        };

        $(document).ready(function() {
            // 초기 진입 시 목록 로드
            router.goList();

            // [이벤트 위임] 동적으로 로드된 콘텐츠 내부의 링크 처리
            
            // 1. 게시글 제목 클릭 (상세보기)
            $(document).on('click', '.link-detail', function(e) {
                e.preventDefault();
                const id = $(this).data('id');
                router.goPost(id);
            });

            // 2. 글쓰기 버튼 클릭
            $(document).on('click', '.link-write', function(e) {
                e.preventDefault();
                router.goPost(); // ID 없음 -> 작성 모드
            });

            // 3. 목록으로 돌아가기
            $(document).on('click', '.link-list', function(e) {
                e.preventDefault();
                router.goList();
            });

            // 4. 사이드바 메뉴 클릭 처리
            $('.sidebar-menu a, .btn-write').on('click', function(e) {
                e.preventDefault();
                
                // 메뉴 활성화 UI
                $('.sidebar-menu li').removeClass('active');
                if($(this).parent('li').length) $(this).parent('li').addClass('active');

                // 라우팅
                const type = $(this).data('link');
                if(type === 'write') router.goPost();
                else router.goList();
            });
        });
    </script>
</body>
</html>