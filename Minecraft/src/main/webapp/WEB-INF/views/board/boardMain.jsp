<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>커뮤니티 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        .loading-spinner { display: none; text-align: center; padding: 50px; font-weight: bold; color: #666; }
        #dynamic-content { min-height: 500px; }
    </style>
</head>
<body>

    <%@ include file="../header.jsp" %> 

    <main class="container">
        <div class="board-layout">
            
            <jsp:include page="boardSidebar.jsp" />

            <section class="board-main">
                <div id="loading" class="loading-spinner">
                    <p>데이터를 불러오는 중입니다...</p>
                </div>
                <div id="dynamic-content"></div>
            </section>
        </div>
    </main>

    <%@ include file="../footer.jsp" %>

    <script>
        const contextPath = '${pageContext.request.contextPath}';

        const router = {
            target: $('#dynamic-content'),
            loader: $('#loading'),
            currentCategory: 'ALL', 

            load: function(url) {
                this.target.empty(); 
                this.loader.show();
                this.target.load(url, function(response, status, xhr) {
                    router.loader.hide();
                    if (status == "error") {
                        router.target.html("<div style='padding:50px; text-align:center;'>오류 발생: " + xhr.status + "</div>");
                    }
                });
            },

            goList: function(page, newCategory) {
                const p = page ? page : 1;
                if (newCategory) this.currentCategory = newCategory;
                let url = contextPath + "/board/list?page=" + p + "&category=" + this.currentCategory;
                this.load(url);
            },

            goPost: function(id) {
                let url = contextPath + "/board/view";
                if(id) url += "?id=" + id;
                this.load(url);
            }
        };

        $(document).ready(function() {
            // [핵심] URL 파라미터(viewId) 체크하여 상세페이지 자동 로드
            const urlParams = new URLSearchParams(window.location.search);
            const viewId = urlParams.get('viewId');

            if (viewId) {
                router.goPost(viewId);
            } else {
                router.goList(1, 'ALL');
            }

            $(document).on('click', '.sidebar-menu a', function(e) {
                e.preventDefault();
                $('.sidebar-menu li').removeClass('active');
                $(this).parent('li').addClass('active');
                const category = $(this).data('category');
                router.goList(1, category);
            });

            $(document).on('click', '.link-detail', function(e) {
                e.preventDefault();
                const id = $(this).data('id');
                router.goPost(id);
            });

            $(document).on('click', '.link-write', function(e) {
                e.preventDefault();
                router.goPost();
            });
        });
    </script>
</body>
</html>